package com.emergency.service;

import com.emergency.model.EmergencyDispatch;
import com.emergency.model.PoliceEmergency;

public class PoliceStation implements EmergencyDispatcher {

    @Override
    public void dispatch(EmergencyDispatch incident) {
        PoliceEmergency police = (PoliceEmergency) incident;
        logger.info("[DISPATCH - POLICE] Patrol units dispatched. Weapon: {}", police.isWeaponInvolved());
    }

}
