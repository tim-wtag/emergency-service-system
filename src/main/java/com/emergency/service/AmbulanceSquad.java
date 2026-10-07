package com.emergency.service;

import com.emergency.model.EmergencyDispatch;
import com.emergency.model.MedicalEmergency;

public class AmbulanceSquad implements EmergencyDispatcher {

    @Override
    public void dispatch(EmergencyDispatch incident) {
        MedicalEmergency medical = (MedicalEmergency) incident;
        logger.info("[DISPATCH - MEDICAL] Paramedics deployed. Patients: {}" ,medical.getPatientCount());
    }

}
