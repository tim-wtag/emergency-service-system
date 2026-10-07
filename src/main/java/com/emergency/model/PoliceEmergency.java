package com.emergency.model;

public class PoliceEmergency extends EmergencyDispatch {
    private boolean weaponInvolved;

    public PoliceEmergency(boolean weaponInvolved, EmergencyIncident incident) {
        super(EmergencyType.POLICE, incident);
        this.weaponInvolved = weaponInvolved;
    }

    public boolean isWeaponInvolved() {
        return weaponInvolved;
    }

    public void setWeaponInvolved(boolean weaponInvolved){
        this.weaponInvolved = weaponInvolved;
    }
}
