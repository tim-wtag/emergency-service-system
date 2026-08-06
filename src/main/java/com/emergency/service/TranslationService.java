package com.emergency.service;

public class TranslationService {

    // private String[] englishKeywords = new String[] { "fire", "bleed", "theft" };
    // private String[] frenchKeywords = new String[] { "incendie", "feu",
    // "blessure", "sang", "vol" };
    private static final String[][] EMERGENCY_MAP = { { "feuer", "fire" }, { "feu", "fire" }, { "incendie", "fire" },
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

            translated = translated.replaceAll(keyword, value);
        }

        return translated;
    }

}
