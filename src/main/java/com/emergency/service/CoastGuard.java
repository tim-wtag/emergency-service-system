package com.emergency.service;

import com.emergency.model.EmergencyDispatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CoastGuard implements EmergencyDispatcher {

    private static final Logger logger = LoggerFactory.getLogger(CoastGuard.class);

    @Override
    public void dispatch(EmergencyDispatch incident) {
        String peopleInDistressStr = incident.getDetail("peopleInDistress");
        
        boolean peopleInDistress = Boolean.parseBoolean(peopleInDistressStr);
        
        logger.info("[DISPATCH - Drowning] Boat deployed. People in distress: {}", peopleInDistress);
    }
}