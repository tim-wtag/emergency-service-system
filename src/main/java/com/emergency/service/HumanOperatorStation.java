package com.emergency.service;

import com.emergency.model.EmergencyIncident;
import com.emergency.model.UnknownIncident;

public class HumanOperatorStation implements EmergencyDispatcher {

    @Override
    public void dispatch(EmergencyIncident incident) {
        UnknownIncident unclear = (UnknownIncident) incident;
        logger.info("[DISPATCH - OPERATOR] Alert unclear. Forwarding raw description to human operator: " + unclear.isPrankCall());
    }

}