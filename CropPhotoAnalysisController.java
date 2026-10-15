package com.agritech.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/ai")
public class CropPhotoAnalysisController {
    private static final long MAX_IMAGE_BYTES = 8L * 1024 * 1024;
    private final ObjectMapper mapper = new ObjectMapper();
    private final HttpClient http = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(20)).build();

    @PostMapping(value = "/crop-analysis", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Map<String, String> analyzeCropPhoto(
            @RequestParam("photo") MultipartFile photo,
            @RequestParam(defaultValue = "Unknown") String crop,
            @RequestParam(defaultValue = "Unknown") String growthStage,
            @RequestParam(defaultValue = "Not provided") String soilInfo,
            @RequestParam(defaultValue = "en") String language) {

        String apiKey = System.getenv("OPENAI_API_KEY");
        if (apiKey == null || apiKey.isBlank()) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                    "AI is not configured. Set the OPENAI_API_KEY environment variable in the backend terminal.");
        }
        if (photo == null || photo.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Choose a crop photo first.");
        }
        if (photo.getSize() > MAX_IMAGE_BYTES) {
            throw new ResponseStatusException(HttpStatus.PAYLOAD_TOO_LARGE, "Image must be 8 MB or smaller.");
        }
        String mime = photo.getContentType();
        if (mime == null || !(mime.equals("image/jpeg") || mime.equals("image/png") || mime.equals("image/webp"))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Use a JPG, PNG, or WEBP image.");
        }

        try {
            String base64 = Base64.getEncoder().encodeToString(photo.getBytes());
            String dataUrl = "data:" + mime + ";base64," + base64;
            String prompt = "You are an agricultural information assistant for smallholder farmers in Telangana, India. " +
                    "Inspect the crop photo cautiously. Crop reported by farmer: " + crop + ". Growth stage: " + growthStage +
                    ". Soil/test information: " + soilInfo + ". Reply in " + ("te".equalsIgnoreCase(language) ? "simple Telugu" : "simple English") +
                    ". Give a concise report with these headings: (1) What is visible, (2) Possible issue(s) and confidence (low/medium/high), " +
                    "(3) Other possible causes, (4) Safe immediate steps, (5) Fertilizer/nutrient guidance, (6) What to check next. " +
                    "Do not claim certainty from an image alone. Do not prescribe exact chemical pesticide or fertilizer doses from the photo. " +
                    "Explain that fertilizer selection and quantity require crop, growth stage, soil test and local recommendations. " +
                    "If symptoms are unclear, say so and ask for a clearer photo and soil test. Do not invent lab results, market facts or diagnosis. " +
                    "Mention contacting a local agriculture officer/KVK for severe or spreading symptoms. This is guidance, not a confirmed diagnosis.";

            Map<String, Object> imagePart = Map.of("type", "input_image", "image_url", dataUrl, "detail", "auto");
            Map<String, Object> textPart = Map.of("type", "input_text", "text", prompt);
            Map<String, Object> userMessage = Map.of("role", "user", "content", new Object[]{textPart, imagePart});
            Map<String, Object> payload = Map.of("model", "gpt-4.1-mini", "input", new Object[]{userMessage}, "max_output_tokens", 900);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.openai.com/v1/responses"))
                    .timeout(Duration.ofSeconds(90))
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(payload)))
                    .build();
            HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                throw new ResponseStatusException(HttpStatus.BAD_GATEWAY,
                        "The AI provider returned an error (HTTP " + response.statusCode() + "). Check your API key and account usage.");
            }
            JsonNode root = mapper.readTree(response.body());
            StringBuilder output = new StringBuilder();
            JsonNode outputNodes = root.path("output");
            for (JsonNode item : outputNodes) {
                for (JsonNode content : item.path("content")) {
                    if ("output_text".equals(content.path("type").asText())) {
                        if (!output.isEmpty()) output.append("\n\n");
                        output.append(content.path("text").asText());
                    }
                }
            }
            if (output.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "The AI returned no readable analysis. Please try another clear photo.");
            }
            Map<String, String> result = new LinkedHashMap<>();
            result.put("analysis", output.toString());
            result.put("notice", "AI output can be wrong. Confirm disease and fertilizer decisions with a soil test or local agricultural expert.");
            return result;
        } catch (ResponseStatusException ex) {
            throw ex;
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new ResponseStatusException(HttpStatus.GATEWAY_TIMEOUT, "AI analysis was interrupted. Please try again.");
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Could not analyze the photo. Check the backend connection and try again.");
        }
    }
}
