package com.emergency.service;

import com.emergency.model.EmergencyDispatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PoliceStation implements EmergencyDispatcher {

    private static final Logger logger = LoggerFactory.getLogger(PoliceStation.class);

    @Override
    public void dispatch(EmergencyDispatch incident) {
        String weaponInvolvedStr = incident.getDetail("weaponInvolved");
        boolean weaponInvolved = Boolean.parseBoolean(weaponInvolvedStr);
        
        logger.info("[DISPATCH - POLICE] Patrol units dispatched. Weapon: {}", weaponInvolved);
    }

}