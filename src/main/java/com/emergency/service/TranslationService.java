package com.emergency.service;

public class TranslationService {

    // private String[] englishKeywords = new String[] { "fire", "bleed", "theft" };
    // private String[] frenchKeywords = new String[] { "incendie", "feu",
    // "blessure", "sang", "vol" };
    private static final String[][] EMERGENCY_MAP = { { "feu", "fire" }, { "agni", "fire" }, { "fuego", "fire" } };

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
