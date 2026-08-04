package com.emergency.service;

import com.emergency.model.EmergencyIncident;
import com.emergency.model.FireIncident;

public class FireStation implements EmergencyDispatcher {
    @Override
    public void dispatch(EmergencyIncident incident) {
        FireIncident fire = (FireIncident) incident;
        System.out.println("[DISPATCH - FIRE] Routing engines. Hazmat: " + fire.isHazmatInvolved());
    }
}
