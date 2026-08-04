package com.emergency.service;

import com.emergency.model.EmergencyIncident;

public interface EmergencyDispatcher {
    void dispatch(EmergencyIncident incident);
}
