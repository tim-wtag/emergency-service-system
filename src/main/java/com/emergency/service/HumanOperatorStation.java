package com.emergency.service;

import com.emergency.model.EmergencyDispatch;
import com.emergency.model.UnknownEmergency;

public class HumanOperatorStation implements EmergencyDispatcher {

    @Override
    public void dispatch(EmergencyDispatch incident) {
        UnknownEmergency unclear = (UnknownEmergency) incident;
        logger.info("[DISPATCH - OPERATOR] Alert unclear. Forwarding raw description to human operator: {}", unclear.isPrankCall());
    }

}