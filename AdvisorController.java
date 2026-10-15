package com.agritech.controller;
import org.springframework.web.bind.annotation.*;
import java.util.*;
@RestController
@RequestMapping("/api/advisor")
public class AdvisorController {
 @GetMapping("/irrigation")
 public Map<String,Object> irrigation(@RequestParam(defaultValue="medium") String soil,
   @RequestParam(defaultValue="normal") String weather, @RequestParam(defaultValue="general") String crop) {
  String frequency, amount, note;
  if ("rainy".equalsIgnoreCase(weather)) {
   frequency="Check soil before watering"; amount="Skip irrigation if soil is already moist";
   note="Avoid waterlogging and check field drainage.";
  } else if ("sandy".equalsIgnoreCase(soil)) {
   frequency="Smaller, more frequent watering"; amount="Use short irrigation cycles";
   note="Sandy soil drains quickly; confirm moisture before each cycle.";
  } else if ("clay".equalsIgnoreCase(soil)) {
   frequency="Less frequent, slower watering"; amount="Water gradually";
   note="Clay soil holds water longer; avoid standing water.";
  } else {
   frequency="Check soil moisture daily"; amount="Water when the root zone begins to dry";
   note="Adjust according to crop stage and local weather.";
  }
  return Map.of("soil",soil,"weather",weather,"crop",crop,"frequency",frequency,"amount",amount,"note",note,
    "disclaimer","General guidance only. Follow local agricultural officer recommendations.");
 }
 @GetMapping("/crop-plan")
 public Map<String,Object> cropPlan(@RequestParam String soil, @RequestParam String season) {
  String s=soil.toLowerCase(), se=season.toLowerCase();
  List<String> crops;
  if(se.contains("monsoon") || se.contains("kharif")) {
   crops=s.contains("black")?List.of("Cotton","Soybean","Sorghum"):List.of("Maize","Pulses","Groundnut");
  } else if(se.contains("winter") || se.contains("rabi")) {
   crops=s.contains("black")?List.of("Chickpea","Safflower","Wheat (where suitable)"):List.of("Chickpea","Mustard","Vegetables");
  } else {
   crops=List.of("Short-duration vegetables","Green gram (where suitable)","Fodder crops");
  }
  return Map.of("soil",soil,"season",season,"suggestions",crops,
    "note","Suggestions are indicative. Confirm local variety, water availability, soil test and sowing window before planting.");
 }
 @GetMapping("/market-guide")
 public List<Map<String,String>> marketGuide() {
  return List.of(
   Map.of("crop","Paddy","guide","Check the current local mandi rate and moisture deductions before selling."),
   Map.of("crop","Cotton","guide","Compare nearby market rates, quality grade and transport cost."),
   Map.of("crop","Maize","guide","Compare local buyer offers and storage cost before choosing a sale date."),
   Map.of("crop","Chickpea","guide","Check current mandi arrivals, grade and minimum support price announcements.")
  );
 }
}
