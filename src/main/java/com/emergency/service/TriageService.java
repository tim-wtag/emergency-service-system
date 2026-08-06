package com.emergency.service;

import com.emergency.exception.EmptyAlertException;
import com.emergency.model.CoastGuardIncident;
import com.emergency.model.EmergencyIncident;
import com.emergency.model.FireIncident;
import com.emergency.model.MedicalIncident;
import com.emergency.model.PoliceIncident;
import com.emergency.model.UnknownIncident;

public class TriageService {
    public EmergencyIncident parse(String input) {
        if (input == null || input.trim().isEmpty()) {
            throw new EmptyAlertException("Cannot triage empty alert.");
        }

        if (input.contains("fire")) {
            return new FireIncident(input, false);
        } else if (input.contains("bleed") || input.contains("doctor") || input.contains("ambulance")) {
            return new MedicalIncident(input, 2);
        } else if (input.contains("theft") || input.contains("crime")) {
            return new PoliceIncident(input, false);
        } else if (input.contains("drowning")) {
            return new CoastGuardIncident(input, false);
        } else {
            return new UnknownIncident(input, false);
        }

    }
}
