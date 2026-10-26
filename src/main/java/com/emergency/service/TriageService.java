package com.emergency.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.emergency.controller.EmergencyRequest;
import com.emergency.exception.EmptyAlertException;
import com.emergency.model.CoastGuardEmergency;
import com.emergency.model.EmergencyDispatch;
import com.emergency.model.EmergencyIncident;
import com.emergency.model.EmergencyKeyword;
import com.emergency.model.FireEmergency;
import com.emergency.model.MedicalEmergency;
import com.emergency.model.PoliceEmergency;
import com.emergency.model.Status;
import com.emergency.model.UnknownEmergency;
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

        return emergencies;
    }

    public void applyDetails(EmergencyDispatch emergency, EmergencyRequest request) {
        switch (emergency.getType()) {
            case FIRE -> ((FireEmergency) emergency).setHazmatInvolved(request.isHazmatInvolved());
            case MEDICAL -> ((MedicalEmergency) emergency).setPatientCount(request.getPatientCount());

            case POLICE -> ((PoliceEmergency) emergency).setWeaponInvolved(request.isWeaponInvolved());

            case COASTAL -> ((CoastGuardEmergency) emergency).setPeopleInDistress(request.isPeopleInDistress());

            case UNKNOWN -> {
                boolean prankCall = request.isPrankCall();
                ((UnknownEmergency) emergency).setPrankCall(prankCall);

                if (prankCall) {
                    emergency.setStatus(Status.RESOLVED);
                    emergencyRepository.markEmergencyAsResolved(emergency.getId());

                } else {
                    emergency.setStatus(Status.PENDING);
                }
            }
        }
    }
}