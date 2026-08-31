package com.emergency.service;

import com.emergency.model.EmergencyIncident;
import com.emergency.model.MedicalIncident;

public class AmbulanceSquad implements EmergencyDispatcher {

    @Override
    public void dispatch(EmergencyIncident incident) {
        MedicalIncident medical = (MedicalIncident) incident;
        logger.info("[DISPATCH - MEDICAL] Paramedics deployed. Patients: " + medical.getPatientCount());
    }

}
