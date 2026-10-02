package com.emergency.model;

public class CoastGuardEmergency extends EmergencyDispatch {
    private boolean peopleInDistress;

    public CoastGuardEmergency(boolean peopleInDistress, EmergencyIncident incident) {
        super(EmergencyType.COASTAL, incident);
        this.peopleInDistress = peopleInDistress;
    }

    public boolean isPeopleInDistress() {
        return peopleInDistress;
    }

    public void setPeopleInDistress(boolean peopleInDistress){
        this.peopleInDistress = peopleInDistress;
    }
}
