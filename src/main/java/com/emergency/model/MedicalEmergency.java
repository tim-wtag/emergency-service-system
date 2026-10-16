package com.emergency.model;

public class MedicalEmergency extends EmergencyDispatch {
    private int patientCount;

    public MedicalEmergency(int patientCount, EmergencyIncident incident) {
        super(EmergencyType.MEDICAL, incident);
        this.patientCount = patientCount;
    }

    public int getPatientCount() {
        return patientCount;
    }

    public void setPatientCount(int patientCount){
        this.patientCount = patientCount;
    }
}
