package com.emergency.model;

public class MedicalIncident extends EmergencyIncident {
    private int patientCount;

    public MedicalIncident(String description, IncidentType type, int patientCount) {
        super(description, IncidentType.MEDICAL);
        this.patientCount = patientCount;
    }

    public int getPatientCount() {
        return patientCount;
    }
}
