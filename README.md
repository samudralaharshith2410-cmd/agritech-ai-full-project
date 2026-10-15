# Agritech Backend

Java 25 + Spring Boot 3.5.16 + MySQL. This is a fresh project and uses its own `agritech` database; it does not modify the older `agriculture` database.

## Configure MySQL
Set your local password in the terminal before starting the app:
- PowerShell: `$env:DB_PASSWORD = Read-Host "Enter your MySQL password"`
- If your MySQL account has no password, you can leave DB_PASSWORD unset.

## Run
From this folder:
`mvn spring-boot:run`

API base URL: `http://localhost:8080/api`

Endpoints:
- GET/POST `/api/tasks`; PUT `/api/tasks/{id}/complete`; DELETE `/api/tasks/{id}`
- GET/POST `/api/expenses`; GET `/api/expenses/summary`; DELETE `/api/expenses/{id}`
- GET `/api/advisor/irrigation?soil=sandy&weather=normal&crop=maize`
- GET `/api/advisor/crop-plan?soil=black&season=monsoon`
- GET `/api/advisor/market-guide`

Note: market guidance is informational and not live market pricing. Irrigation and crop suggestions are general guidance, not a substitute for local expert advice.

## AI crop-photo analysis (optional)
This feature sends a farmer-selected crop image to the OpenAI Responses API and returns possible visible disease/nutrient issues plus cautious next-step guidance. It does not save the photo or AI result in MySQL and does not modify the older `agriculture` database.

1. Create/configure an OpenAI API key in the provider account. API usage may cost money; keep the key private and never put it in React code or commit it to GitHub.
2. In the same PowerShell terminal you use to start the backend, enter the key without echoing it:
   ```powershell
   $secure = Read-Host "OpenAI API key" -AsSecureString
   $env:OPENAI_API_KEY = [System.Net.NetworkCredential]::new("", $secure).Password
   ```
3. Set the MySQL password in that same terminal if needed, then run `mvn spring-boot:run`.
4. In the frontend, open **AI crop scan**, select crop and growth stage, upload a JPG/PNG/WEBP up to 8 MB, and click **Analyze photo with AI**.

Endpoint: `POST /api/ai/crop-analysis` (multipart fields: `photo`, `crop`, `growthStage`, `soilInfo`, `language`). The backend accepts only JPEG/PNG/WEBP and does not persist uploads. Crop images are sent to the configured AI provider for analysis. AI output is not a confirmed diagnosis; fertilizer selection/quantity should be checked against a soil test and local agriculture guidance.
