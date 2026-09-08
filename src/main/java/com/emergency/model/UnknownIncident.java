package com.emergency.model;

public class UnknownIncident extends EmergencyIncident {
    private final boolean prankCall;

    public UnknownIncident(String description, boolean prankCall) {
        super(description, IncidentType.UNKNOWN);
        this.prankCall = prankCall;
    }

    public boolean isPrankCall() {
        return prankCall;
    }

}
