package com.emergency.service;

import com.emergency.model.EmergencyDispatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AmbulanceSquad implements EmergencyDispatcher {

    private static final Logger logger = LoggerFactory.getLogger(AmbulanceSquad.class);

    @Override
    public void dispatch(EmergencyDispatch incident) {
        String patientCountStr = incident.getDetail("patientCount");
        
        int patientCount = (patientCountStr != null) ? Integer.parseInt(patientCountStr) : 0;
        
        logger.info("[DISPATCH - MEDICAL] Paramedics deployed. Patients: {}", patientCount);
    }
}