package com.emergency.model;

public class CoastGuardIncident extends EmergencyIncident {
    private final boolean peopleInDistress;

    public CoastGuardIncident(String description, boolean peopleInDistress) {
        super(description, IncidentType.COASTAL);
        this.peopleInDistress = peopleInDistress;
    }

    public boolean isPeopleInDistress() {
        return peopleInDistress;
    }
}
