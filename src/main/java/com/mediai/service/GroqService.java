package com.mediai.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

@Service
public class GroqService {

    @Value("${groq.api.key}")
    private String apiKey;

    @Value("${groq.api.url}")
    private String apiUrl;

    private final ObjectMapper mapper = new ObjectMapper();

    public GroqResponse getSuggestions(String symptoms) {
        try {
            String prompt = "You are a helpful medical assistant for a pharmacy app in India. "
                    + "A user says: " + symptoms + ". "
                    + "1. Give a short friendly response 2-3 sentences about their symptoms. "
                    + "2. List medicine names commonly used in India for these symptoms. "
                    + "IMPORTANT: End your response with a line starting exactly with: "
                    + "MEDICINES: medicine1, medicine2, medicine3 "
                    + "Only list generic or common Indian brand names like "
                    + "Paracetamol, Dolo 650, Crocin, Cetirizine, Azithromycin. "
                    + "List 3 to 6 medicines maximum. "
                    + "Always add: Please consult a doctor before taking any medicine.";

            // Build Groq request body (OpenAI-compatible format)
            String requestBody = "{"
                    + // ✅ NEW - current working model
                     "\"model\": \"llama-3.3-70b-versatile\","
                    + "\"messages\": [{"
                    + "\"role\": \"user\","
                    + "\"content\": \"" + prompt.replace("\"", "'") + "\""
                    + "}],"
                    + "\"max_tokens\": 500,"
                    + "\"temperature\": 0.7"
                    + "}";

            HttpClient client = HttpClient.newHttpClient();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(apiUrl))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + apiKey.trim())
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpResponse<String> response = client.send(request,
                    HttpResponse.BodyHandlers.ofString());

            System.out.println("=== GROQ API ===");
            System.out.println("Status: " + response.statusCode());

            if (response.statusCode() != 200) {
                System.err.println("Groq Error: " + response.body());
                return new GroqResponse(
                        "AI error (status " + response.statusCode() + "): " + response.body(),
                        new ArrayList<>());
            }

            // Parse OpenAI-compatible response
            JsonNode root = mapper.readTree(response.body());
            String text = root
                    .path("choices").get(0)
                    .path("message")
                    .path("content").asText();

            System.out.println("AI Text: " + text);

            // Extract MEDICINES: line
            List<String> medicineNames = new ArrayList<>();
            String[] lines = text.split("\n");
            for (String line : lines) {
                if (line.trim().toUpperCase().startsWith("MEDICINES:")) {
                    String medPart = line.substring(line.indexOf(":") + 1).trim();
                    String[] meds = medPart.split(",");
                    for (String med : meds) {
                        String cleaned = med.trim()
                                .replaceAll("\\*", "")
                                .replaceAll("\\.", "")
                                .trim();
                        if (!cleaned.isEmpty()) {
                            medicineNames.add(cleaned);
                        }
                    }
                }
            }

            System.out.println("Medicines: " + medicineNames);

            String displayText = text.replaceAll("(?i)MEDICINES:.*", "").trim();

            return new GroqResponse(displayText, medicineNames);

        } catch (Exception e) {
            System.err.println("=== GROQ ERROR ===");
            e.printStackTrace();
            return new GroqResponse("Error: " + e.getMessage(), new ArrayList<>());
        }
    }

    public static class GroqResponse {
        private String message;
        private List<String> medicineNames;

        public GroqResponse(String message, List<String> medicineNames) {
            this.message = message;
            this.medicineNames = medicineNames;
        }

        public String getMessage() { return message; }
        public List<String> getMedicineNames() { return medicineNames; }
    }
}