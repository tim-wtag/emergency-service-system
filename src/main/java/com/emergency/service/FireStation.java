package com.emergency.service;

import com.emergency.model.EmergencyDispatch;
import com.emergency.model.FireEmergency;

public class FireStation implements EmergencyDispatcher {
    @Override
    public void dispatch(EmergencyDispatch incident) {
        FireEmergency fire = (FireEmergency) incident;
        logger.info("[DISPATCH - FIRE] Routing engines. Hazmat: {}" ,fire.isHazmatInvolved());
    }
}
