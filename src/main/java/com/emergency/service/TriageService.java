package com.emergency.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.emergency.controller.EmergencyCliController;
import com.emergency.exception.EmptyAlertException;
import com.emergency.model.CoastGuardIncident;
import com.emergency.model.EmergencyIncident;
import com.emergency.model.EmergencyKeyword;
import com.emergency.model.FireIncident;
import com.emergency.model.MedicalIncident;
import com.emergency.model.PoliceIncident;
import com.emergency.model.UnknownIncident;

public class TriageService {
//private static final Logger logger = LoggerFactory.getLogger(TriageService.class);

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
                            addIncident(incidents, new FireIncident(input, false), i++);
                        }
                        case MEDICAL_KEYWORD -> {
                            addIncident(incidents, new MedicalIncident(input, 0), i++);
                        }
                        case POLICE_KEYWORD -> {
                            addIncident(incidents, new PoliceIncident(input, false), i++);
                        }
                        case COASTAL_KEYWORD -> {
                            addIncident(incidents, new CoastGuardIncident(input, false), i++);
                            
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

    private void addIncident(EmergencyIncident[] incidents, EmergencyIncident incident, Integer i) {
        for (EmergencyIncident inc : incidents) {
            if (inc != null && inc.getType().equals(incident.getType())) {
                return;
            }
        }
        incidents[i] = incident;
        //i = i+1;
    }
}
