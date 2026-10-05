package com.emergency.service;

import java.util.ArrayList;
import java.util.List;

import com.emergency.exception.EmptyAlertException;
import com.emergency.model.CoastGuardEmergency;
import com.emergency.model.EmergencyDispatch;
import com.emergency.model.EmergencyIncident;
import com.emergency.model.EmergencyKeyword;
import com.emergency.model.FireEmergency;
import com.emergency.model.MedicalEmergency;
import com.emergency.model.PoliceEmergency;
import com.emergency.model.UnknownEmergency;
import com.emergency.repository.EmergencyRepository;
import com.emergency.repository.IncidentRepository;

public class TriageService {

    private final IncidentRepository incidentRepository;
    private final EmergencyRepository emergencyRepository;

    public TriageService(IncidentRepository incidentRepository, EmergencyRepository emergencyRepository) {
        this.incidentRepository = incidentRepository;
        this.emergencyRepository = emergencyRepository;
    }

    public List<EmergencyDispatch> parseAndSave(String input) {
        if (input == null || input.trim().isEmpty()) {
            throw new EmptyAlertException("Cannot triage empty alert.");
        }

        EmergencyIncident mainIncident = new EmergencyIncident(input);
        mainIncident = incidentRepository.save(mainIncident);

        List<EmergencyDispatch> emergencies = new ArrayList<>();

        for (EmergencyKeyword keyword : EmergencyKeyword.values()) {
            for (String kw : keyword.getKeyword()) {
                if (input.toLowerCase().contains(kw.toLowerCase())) {
                    switch (keyword) {
                        case FIRE_KEYWORD -> emergencies.add(new FireEmergency(false, mainIncident));
                        case MEDICAL_KEYWORD -> emergencies.add(new MedicalEmergency(0, mainIncident));
                        case POLICE_KEYWORD -> emergencies.add(new PoliceEmergency(false, mainIncident));
                        case COASTAL_KEYWORD -> emergencies.add(new CoastGuardEmergency(false, mainIncident));
                    }
                    break;
                }
            }
        }

        if (emergencies.isEmpty()) {
            emergencies.add(new UnknownEmergency(false, mainIncident));
        }

        for (EmergencyDispatch emergency : emergencies) {
            emergencyRepository.save(emergency);
        }

        return emergencies;
    }
}