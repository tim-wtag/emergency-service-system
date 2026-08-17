package com.emergency.service;

import com.emergency.exception.EmptyAlertException;
import com.emergency.model.CoastGuardIncident;
import com.emergency.model.EmergencyIncident;
import com.emergency.model.EmergencyKeyword;
import com.emergency.model.FireIncident;
import com.emergency.model.IncidentType;
import com.emergency.model.MedicalIncident;
import com.emergency.model.PoliceIncident;
import com.emergency.model.UnknownIncident;

public class TriageService {
    public EmergencyIncident[] parse(String input) {
        if (input == null || input.trim().isEmpty()) {
            throw new EmptyAlertException("Cannot triage empty alert.");
        }

        EmergencyIncident[] incidents = new EmergencyIncident[5];

        int i = 0;

        for (EmergencyKeyword keyword : EmergencyKeyword.values()) {
            for (String keywords : keyword.getKeyword()) {
                if (input.contains(keywords)) {
                    switch (keyword) {
                        case FIRE_KEYWORD -> {
                            incidents[i++] = new FireIncident(input, false);
                        }
                        case MEDICAL_KEYWORD -> {
                            incidents[i++] = new MedicalIncident(input, 0);
                        }
                        case POLICE_KEYWORD -> {
                            incidents[i++] = new PoliceIncident(input, false);
                        }
                        case COASTAL_KEYWORD -> {
                            incidents[i++] = new CoastGuardIncident(input, false);
                        }
                        default -> {
                            incidents[i++] = new UnknownIncident(input, false);
                        }
                    }

                }
            }
        }
        return incidents;
    }
}
