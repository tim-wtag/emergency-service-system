package com.emergency.service;

import com.emergency.model.EmergencyIncident;

//import com.emergency.model.EmergencyIncident;

public class DispatchRouter {
    public void route(EmergencyIncident incident) {
        switch (incident.getType()) {
            case FIRE -> new FireStation().dispatch(incident);
            case MEDICAL -> new AmbulanceSquad().dispatch(incident);
            case POLICE -> new PoliceStation().dispatch(incident);
            case COASTAL -> new CoastGuard().dispatch(incident);
            case UNKNOWN -> new HumanOperatorStation().dispatch(incident);
            default -> throw new IllegalStateException("Unexpected type: " + incident.getType());
        }
    }
}
