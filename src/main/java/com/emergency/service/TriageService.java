package com.emergency.service;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

import com.emergency.controller.EmergencyRequest;
import com.emergency.exception.EmptyAlertException;
import com.emergency.model.EmergencyDispatch;
import com.emergency.model.EmergencyIncident;
import com.emergency.model.EmergencyKeyword;
import com.emergency.model.EmergencyType;
import com.emergency.model.Status;
import com.emergency.repository.EmergencyRepository;
import com.emergency.repository.IncidentRepository;

@Service
public class TriageService {

    IncidentRepository incidentRepository;
    EmergencyRepository emergencyRepository;

    public TriageService(IncidentRepository incidentRepository, EmergencyRepository emergencyRepository) {
        this.incidentRepository = incidentRepository;
        this.emergencyRepository = emergencyRepository;
    }

    public List<EmergencyDispatch> parse(String input) {
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
                        case FIRE_KEYWORD -> emergencies.add(new EmergencyDispatch(EmergencyType.FIRE, mainIncident));
                        case MEDICAL_KEYWORD -> emergencies.add(new EmergencyDispatch(EmergencyType.MEDICAL, mainIncident));
                        case POLICE_KEYWORD -> emergencies.add(new EmergencyDispatch(EmergencyType.POLICE, mainIncident));
                        case COASTAL_KEYWORD -> emergencies.add(new EmergencyDispatch(EmergencyType.COASTAL, mainIncident));
                    }
                    break;
                }
            }
        }

        if (emergencies.isEmpty()) {
            emergencies.add(new EmergencyDispatch(EmergencyType.UNKNOWN, mainIncident));
        }

        return emergencies;
    }

    public void applyDetails(EmergencyDispatch emergency, EmergencyRequest request) {
        switch (emergency.getType()) {
            case FIRE -> emergency.addDetail("hazmatInvolved", String.valueOf(request.isHazmatInvolved()));
            case MEDICAL -> emergency.addDetail("patientCount", String.valueOf(request.getPatientCount()));
            case POLICE -> emergency.addDetail("weaponInvolved", String.valueOf(request.isWeaponInvolved()));
            case COASTAL -> emergency.addDetail("peopleInDistress", String.valueOf(request.isPeopleInDistress()));
            case UNKNOWN -> {
                boolean prankCall = request.isPrankCall();
                emergency.addDetail("prankCall", String.valueOf(prankCall));

                if (prankCall) {
                    emergency.setStatus(Status.RESOLVED);
                } else {
                    emergency.setStatus(Status.PENDING);
                }
            }
        }
    }
}