package com.emergency.service;

import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service 
public class TranslationService {

    private static final Logger logger = LoggerFactory.getLogger(TranslationService.class);

    private final Map<String, String> translationMap = new LinkedHashMap<>();

    public TranslationService() {
        try (InputStream jsonStream = new ClassPathResource("dictionary.json").getInputStream()) {
            
            Map<String, Map<String, String>> data = new ObjectMapper().readValue(
                jsonStream, 
                new TypeReference<Map<String, Map<String, String>>>() {}
            );

            Map<String, String> tempTranslationMap = new HashMap<>();
            for (Map<String, String> category : data.values()) {
                category.forEach((englishKey, word) -> tempTranslationMap.put(word.toLowerCase(), englishKey));
            }

            tempTranslationMap.keySet().stream()
                    .sorted((a, b) -> Integer.compare(b.length(), a.length()))
                    .forEach(key -> translationMap.put(key, tempTranslationMap.get(key)));
                    
            logger.info("Translation dictionary loaded successfully.");

        } catch (Exception e) {
            logger.error("Failed to load dictionary.json. Ensure it exists in src/main/resources", e);
            throw new RuntimeException("Failed to initialize TranslationService", e);
        }
    }

    public String translateToEnglish(String input) {
        String translated = input.toLowerCase();
        for (Map.Entry<String, String> entry : translationMap.entrySet()) {
            translated = translated.replaceAll("\\b" + entry.getKey() + "\\b", entry.getValue());
        }
        return translated;
    }
}