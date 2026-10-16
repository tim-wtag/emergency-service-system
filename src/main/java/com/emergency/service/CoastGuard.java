package com.emergency.service;

import com.emergency.model.CoastGuardEmergency;
import com.emergency.model.EmergencyDispatch;

public class CoastGuard implements EmergencyDispatcher{

    @Override
    public void dispatch(EmergencyDispatch incident) {
        CoastGuardEmergency coastGuard = (CoastGuardEmergency) incident;
        logger.info("[DISPATCH - Drowning] Boat deployed. People in distress: {}", coastGuard.isPeopleInDistress());
    }

}
