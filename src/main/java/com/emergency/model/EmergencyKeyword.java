package com.emergency.model;

public enum EmergencyKeyword {
    FIRE_KEYWORD("fire"),
    MEDICAL_KEYWORD("bleed", "doctor", "ambulance"),
    POLICE_KEYWORD("crime", "theft"),
    COASTAL_KEYWORD("drowning");

    private final String[] keyword;
    
    EmergencyKeyword(String... keyword){
        this.keyword = keyword;
    }

    public String[] getKeyword(){
        return keyword;
    }
}

