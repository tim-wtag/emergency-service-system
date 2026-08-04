package com.emergency.model;

public class PoliceIncident extends EmergencyIncident {
    private boolean weaponInvolved;

    public PoliceIncident(String description, boolean weaponInvolved) {
        super(description, IncidentType.POLICE);
        this.weaponInvolved = weaponInvolved;
    }

    public boolean isWeaponInvolved() {
        return weaponInvolved;
    }
}
