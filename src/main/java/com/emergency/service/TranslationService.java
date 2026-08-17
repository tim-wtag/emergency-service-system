package com.emergency.service;

import java.util.regex.Pattern;

public class TranslationService {

    private String[][] EMERGENCY_MAP = { { "feuer", "fire" }, { "feu", "fire" }, { "incendie", "fire" },
            { "blessure", "bleed" },
            { "sang", "bleed" }, { "verletzung", "bleed" }, { "blut", "bleed" }, { "docteur", "doctor" },
            { "arzt", "doctor" },
            { "ambulance", "ambulance" }, { "krankenwagen", "ambulance" }, { "vole", "theft" },
            { "diebstahl", "theft" }, { "crime", "crime" }, { "verbrechen", "crime" }, { "noyade", "drowning" },
            { "ertrinken", "drowning" } };

    public String translateToEnglish(String input) {
        String translated = input.toLowerCase();
        for (int i = 0; i < EMERGENCY_MAP.length; i++) {
            String keyword = EMERGENCY_MAP[i][0];
            String value = EMERGENCY_MAP[i][1];

            translated = translated.replaceAll("\\b" + keyword + "\\b", value);
        }

        return translated;
    }

}