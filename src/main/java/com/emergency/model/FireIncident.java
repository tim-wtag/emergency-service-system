package com.emergency.model;

public class FireIncident extends EmergencyIncident {
    private final boolean hazmatInvolved;

    public FireIncident(String description, boolean hazmatInvolved) {
        super(description, IncidentType.FIRE);
        this.hazmatInvolved = hazmatInvolved;
    }

    public boolean isHazmatInvolved() {
        return hazmatInvolved;
    }
}
