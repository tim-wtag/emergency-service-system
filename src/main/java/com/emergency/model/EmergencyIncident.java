package com.emergency.model;

public abstract class EmergencyIncident {
    private String description;
    private IncidentType type;

    public EmergencyIncident(String description, IncidentType type) {
        this.description = description;
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public IncidentType getType() {
        return type;
    }
}
