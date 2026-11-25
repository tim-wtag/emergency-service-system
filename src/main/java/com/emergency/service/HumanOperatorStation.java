package com.emergency.service;

import com.emergency.model.EmergencyDispatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HumanOperatorStation implements EmergencyDispatcher {

    private static final Logger logger = LoggerFactory.getLogger(HumanOperatorStation.class);

    @Override
    public void dispatch(EmergencyDispatch incident) {
        String prankCallStr = incident.getDetail("prankCall");
        boolean prankCall = Boolean.parseBoolean(prankCallStr);
        
        logger.info("[DISPATCH - OPERATOR] Alert unclear. Forwarding raw description to human operator: {}", prankCall);
    }
}