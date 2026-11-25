package com.emergency.service;

import com.emergency.model.EmergencyDispatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FireStation implements EmergencyDispatcher {
    
    private static final Logger logger = LoggerFactory.getLogger(FireStation.class);
    
    @Override
    public void dispatch(EmergencyDispatch incident) {
        boolean hazmat = Boolean.parseBoolean(incident.getDetail("hazmatInvolved"));
        
        logger.info("[DISPATCH - FIRE] Routing engines. Hazmat: {}", hazmat);
    }
}