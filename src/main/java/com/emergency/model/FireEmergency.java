package com.emergency.model;

public class FireEmergency extends EmergencyDispatch {
    private boolean hazmatInvolved;

    public FireEmergency(boolean hazmatInvolved, EmergencyIncident incident) {
        super(EmergencyType.FIRE, incident);
        this.hazmatInvolved = hazmatInvolved;
    }

    public boolean isHazmatInvolved() {
        return hazmatInvolved;
    }

    public void setHazmatInvolved(boolean hazmatInvolved){
        this.hazmatInvolved = hazmatInvolved;
    }
}
