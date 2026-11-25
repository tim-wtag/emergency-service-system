package com.emergency.controller;

public class EmergencyRequest {

    private String description;
    private boolean hazmatInvolved;
    private int patientCount;
    private boolean weaponInvolved;
    private boolean peopleInDistress;
    private boolean prankCall;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isHazmatInvolved() {
        return hazmatInvolved;
    }

    public void setHazmatInvolved(boolean hazmatInvolved) {
        this.hazmatInvolved = hazmatInvolved;
    }

    public int getPatientCount() {
        return patientCount;
    }

    public void setPatientCount(int patientCount) {
        this.patientCount = patientCount;
    }

    public boolean isWeaponInvolved() {
        return weaponInvolved;
    }

    public void setWeaponInvolved(boolean weaponInvolved) {
        this.weaponInvolved = weaponInvolved;
    }

    public boolean isPeopleInDistress() {
        return peopleInDistress;
    }

    public void setPeopleInDistress(boolean peopleInDistress) {
        this.peopleInDistress = peopleInDistress;
    }

    public boolean isPrankCall() {
        return prankCall;
    }

    public void setPrankCall(boolean prankCall) {
        this.prankCall = prankCall;
    }
}
