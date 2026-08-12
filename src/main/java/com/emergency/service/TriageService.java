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

        return switch (input.toLowerCase()) {
            case "fire" -> new FireIncident(input, false);
            case "bleed", "doctor", "ambulance" -> new MedicalIncident(input, 2);
            case "theft", "crime" -> new PoliceIncident(input, false);
            case "drowning" -> new CoastGuardIncident(input, false);
            default -> new UnknownIncident(input, false);
        };
    }
}
