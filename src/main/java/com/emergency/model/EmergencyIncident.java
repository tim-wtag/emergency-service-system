package com.emergency.model;

public abstract class EmergencyIncident {
    private final String description;
    private final IncidentType type;

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
