package com.emergency.service;

import java.util.ArrayList;
import java.util.List;

import com.emergency.exception.EmptyAlertException;
import com.emergency.model.CoastGuardIncident;
import com.emergency.model.EmergencyIncident;
import com.emergency.model.EmergencyKeyword;
import com.emergency.model.FireIncident;
import com.emergency.model.MedicalIncident;
import com.emergency.model.PoliceIncident;
import com.emergency.model.UnknownIncident;

public class TriageService {
    private boolean addIncident(List<EmergencyIncident> incidents, EmergencyIncident incident) {
        for (EmergencyIncident inc : incidents) {
            if (inc.getType().equals(incident.getType())) {
                return false;
            }
        }
        
        incidents.add(incident);
        return true;
    }

    public List<EmergencyIncident> parse(String input) {
        if (input == null || input.trim().isEmpty()) {
            throw new EmptyAlertException("Cannot triage empty alert.");
        }

        List<EmergencyIncident> incidents = new ArrayList<>();

        for (EmergencyKeyword keyword : EmergencyKeyword.values()) {
            for (String keywords : keyword.getKeyword()) {
                if (input.contains(keywords)) {
                    switch (keyword) {
                        case FIRE_KEYWORD -> addIncident(incidents, new FireIncident(input, false));
                        case MEDICAL_KEYWORD -> addIncident(incidents, new MedicalIncident(input, 0));
                        case POLICE_KEYWORD -> addIncident(incidents, new PoliceIncident(input, false));
                        case COASTAL_KEYWORD -> addIncident(incidents, new CoastGuardIncident(input, false));
                    }
                }
            }
        }

        if (incidents.isEmpty()) {
            incidents.add(new UnknownIncident(input, false));
        }
        
        return incidents;
    }

}
