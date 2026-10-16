package com.emergency.model;

public class UnknownEmergency extends EmergencyDispatch {
    private boolean prankCall;

    public UnknownEmergency(boolean prankCall, EmergencyIncident incident) {
        super(EmergencyType.UNKNOWN, incident);
        this.prankCall = prankCall;
    }

    public boolean isPrankCall() {
        return prankCall;
    }

    public void setPrankCall(boolean prankCall){
        this.prankCall = prankCall;
    }

}
