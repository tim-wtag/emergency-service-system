package com.emergency.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.emergency.model.EmergencyIncident;

public interface EmergencyDispatcher {
    Logger logger = LoggerFactory.getLogger(EmergencyDispatcher.class);
    void dispatch(EmergencyIncident incident);
}
