package com.emergency.service;

import com.emergency.model.EmergencyIncident;
import com.emergency.model.PoliceIncident;

public class PoliceStation implements EmergencyDispatcher {

    @Override
    public void dispatch(EmergencyIncident incident) {
        PoliceIncident police = (PoliceIncident) incident;
        System.out.println("[DISPATCH - POLICE] Patrol units dispatched. Weapon: " + police.isWeaponInvolved());
    }

}
