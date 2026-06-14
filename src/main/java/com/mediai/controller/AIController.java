package com.mediai.controller;

import com.mediai.entity.Medicine;
import com.mediai.service.GroqService;
import com.mediai.service.MedicineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ai")
public class AIController {

    @Autowired private GroqService groqService;
    @Autowired private MedicineService medicineService;

    @PostMapping("/suggest")
    public ResponseEntity<?> suggest(@RequestBody Map<String, String> body) {
        String symptoms = body.get("symptoms");
        if (symptoms == null || symptoms.isBlank()) {
            return ResponseEntity.badRequest()
                .body(Map.of("message", "Please describe your symptoms"));
        }

        // Call Groq AI
        GroqService.GroqResponse aiResponse = groqService.getSuggestions(symptoms);

        // Find matching medicines in DB
        List<Medicine> medicines = medicineService.findByAISuggestions(
                aiResponse.getMedicineNames());

        return ResponseEntity.ok(Map.of(
            "message",        aiResponse.getMessage(),
            "suggestedNames", aiResponse.getMedicineNames(),
            "medicines",      medicines
        ));
    }
}