package com.emergency.model;

public class CoastGuardIncident extends EmergencyIncident {
    private boolean peopleInDistress;

    public CoastGuardIncident(String description, IncidentType type, boolean peopleInDistress) {
        super(description, IncidentType.COSTAL);
        this.peopleInDistress = peopleInDistress;
    }

    public boolean isPeopleInDistress() {
        return peopleInDistress;
    }
}
