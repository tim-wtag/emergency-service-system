package com.emergency.service;

public class TranslationService {

    private static final String[][] EMERGENCY_MAP = {{"feuer", "fire"}, {"feu", "fire"}, {"incendie", "fire"},
            {"blessure", "bleed"},
            {"sang", "bleed"}, {"verletzung", "bleed"}, {"blut", "bleed"}, {"docteur", "doctor"},
            {"arzt", "doctor"},
            {"ambulance", "ambulance"}, {"krankenwagen", "ambulance"}, {"vole", "theft"},
            {"diebstahl", "theft"}, {"crime", "crime"}, {"verbrechen", "crime"}, {"noyade", "drowning"},
            {"ertrinken", "drowning"}};

    public String translateToEnglish(String input) {
        String translated = input.toLowerCase();
        for (String[] strings : EMERGENCY_MAP) {
            String keyword = strings[0];
            String value = strings[1];

            translated = translated.replaceAll(keyword, value);
        }

        return translated;
    }

}
