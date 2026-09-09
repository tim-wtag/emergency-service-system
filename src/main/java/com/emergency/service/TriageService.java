package com.emergency.service;

import java.util.concurrent.atomic.AtomicInteger;

import com.emergency.exception.EmptyAlertException;
import com.emergency.model.CoastGuardIncident;
import com.emergency.model.EmergencyIncident;
import com.emergency.model.EmergencyKeyword;
import com.emergency.model.FireIncident;
import com.emergency.model.MedicalIncident;
import com.emergency.model.PoliceIncident;
import com.emergency.model.UnknownIncident;

public class TriageService {
    private final AtomicInteger id = new AtomicInteger(0);

    private boolean addIncident(EmergencyIncident[] incidents, EmergencyIncident incident, Integer i) {
        for (EmergencyIncident inc : incidents) {
            if (inc != null && inc.getType().equals(incident.getType())) {
                return false;
            }
        }
        incidents[i] = incident;
        return true;
    }

    public EmergencyIncident[] parse(String input) {
        if (input == null || input.trim().isEmpty()) {
            throw new EmptyAlertException("Cannot triage empty alert.");
        }

        EmergencyIncident[] incidents = new EmergencyIncident[EmergencyKeyword.values().length + 1];

        Integer i = 0;

        for (EmergencyKeyword keyword : EmergencyKeyword.values()) {
            for (String keywords : keyword.getKeyword()) {
                if (input.contains(keywords)) {
                    switch (keyword) {
                        case FIRE_KEYWORD -> {
                            if (addIncident(incidents, new FireIncident(input, false), i)) {
                                i++;
                            }
                        }
                        case MEDICAL_KEYWORD -> {
                            if (addIncident(incidents, new MedicalIncident(input, 0), i)) {
                                i++;
                            }
                        }
                        case POLICE_KEYWORD -> {
                            if (addIncident(incidents, new PoliceIncident(input, false), i)) {
                                i++;
                            }
                        }
                        case COASTAL_KEYWORD -> {
                            if (addIncident(incidents, new CoastGuardIncident(input, false), i)) {
                                i++;
                            }
                        }
                    }

                }
            }
        }

        if (incidents[0] == null) {
            incidents[0] = new UnknownIncident(input, false);
        }
        return incidents;
    }

}
