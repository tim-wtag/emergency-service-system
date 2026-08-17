package com.emergency.service;

import com.emergency.model.CoastGuardIncident;
import com.emergency.model.EmergencyIncident;

public class CoastGuard implements EmergencyDispatcher{

    @Override
    public void dispatch(EmergencyIncident incident) {
        CoastGuardIncident coastGuard = (CoastGuardIncident) incident;
        logger.info("[DISPATCH - Drowning] Boat deployed. People in distress: " +coastGuard.isPeopleInDistress());
    }

}
