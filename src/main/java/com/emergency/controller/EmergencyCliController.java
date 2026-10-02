package com.emergency.controller;

import java.util.List;
import java.util.Map;
import java.util.Scanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.emergency.model.CoastGuardEmergency;
import com.emergency.model.EmergencyDispatch;
import com.emergency.model.FireEmergency;
import com.emergency.model.Status;
import com.emergency.model.MedicalEmergency;
import com.emergency.model.PoliceEmergency;
import com.emergency.model.UnknownEmergency;
import com.emergency.repository.EmergencyRepository;
import com.emergency.repository.IncidentRepository;
import com.emergency.service.DispatchRouter;
import com.emergency.service.TranslationService;
import com.emergency.service.TriageService;

public class EmergencyCliController {

    private static final Logger logger = LoggerFactory.getLogger(EmergencyCliController.class);
    private TranslationService translationService = null;
    private final TriageService triageService;
    private final DispatchRouter dispatchRouter;
    private final EmergencyInputHelper helper;
    private final IncidentRepository incidentRepository;
    private final EmergencyRepository emergencyRepository;

    public EmergencyCliController() {
        try {
            translationService = new TranslationService("src/main/resources/dictionary.json");
        } catch (Exception e) {
            logger.error("", e);
        }
        triageService = new TriageService();
        dispatchRouter = new DispatchRouter();
        helper = new EmergencyInputHelper();
        incidentRepository = new IncidentRepository();
        emergencyRepository = new EmergencyRepository();

    }

    private void displayStatus() {
        Map<String, Integer> dbCounts = incidentRepository.getStatusCounts();
        
        int pending = dbCounts.getOrDefault("pending", 0);
        int dispatched = dbCounts.getOrDefault("dispatched", 0);
        int resolved = dbCounts.getOrDefault("resolved", 0);
        
        int activeCount = pending + dispatched;
        int totalCount = activeCount + resolved;

        logger.info("Total Incidents : {}", totalCount);
        logger.info("Active          : {} (Pending: {}, Dispatched: {})", activeCount, pending, dispatched);
        logger.info("Resolved        : {}", resolved);
    }


    public void displayActive() {
        logger.info("ACTIVE INCIDENTS: ");
        
        List<String> activeIncidents = incidentRepository.getActiveIncidentsFormatted();
        
        if (activeIncidents.isEmpty()) {
            logger.info("No active incidents found.");
        } else {
            for (String incidentInfo : activeIncidents) {
                logger.info(incidentInfo);
            }
        }
    }

    private void displayEmergencyStatus() {
        Map<String, Integer> emergencyCounts = emergencyRepository.getStatusCounts();
        
        int pending = emergencyCounts.getOrDefault("pending", 0);
        int dispatched = emergencyCounts.getOrDefault("dispatched", 0);
        int resolved = emergencyCounts.getOrDefault("resolved", 0);
        
        int activeCount = pending + dispatched;
        int totalCount = activeCount + resolved;

        logger.info("Total Emergencies : {}", totalCount);
        logger.info("Active           : {} (Pending: {}, Dispatched: {})", activeCount, pending, dispatched);
        logger.info("Resolved         : {}", resolved);
    }

    

   private void handleResolved(Scanner scanner) {
        while (true) {
            logger.info("Please enter the ID of the incident to resolve (or type 'exit' to cancel): ");
            String idText = scanner.nextLine().trim();

            if (idText.equalsIgnoreCase("exit")) {
                logger.info("Canceling resolve operation.");
                return;
            } else if (idText.isEmpty()) {
                logger.info("Error: No ID provided. Please try again.");
            } else {
                try {
                    java.util.UUID targetId = java.util.UUID.fromString(idText);
                    
                    String currentStatus = incidentRepository.getIncidentStatus(targetId);

                    if (currentStatus == null) {
                        logger.info("Error: No incident found with ID {}. Please try again.", targetId);
                    } else if ("resolved".equalsIgnoreCase(currentStatus)) {
                        logger.info("Incident ID {} has already been resolved. Please enter a different ID.", targetId);
                    } else if (incidentRepository.markAsResolved(targetId)) {
                        logger.info("Success: Incident ID {} has been marked as RESOLVED.", targetId);
                        return; 
                    } else {
                        logger.error("Database error: Failed to update Incident ID {}.", targetId);
                    }
                } 
                catch (IllegalArgumentException e) {
                    logger.info("Invalid format. The ID must be a valid UUID (e.g., 123e4567-e89b-12d3-a456-426614174000).", e);
                }
            }
        }
    }

    public void displayResolved() {
        logger.info("RESOLVED INCIDENTS: ");
        List<String> resolvedIncidents = incidentRepository.getResolvedIncidentsFormatted();
        
        if (resolvedIncidents.isEmpty()) {
            logger.info("No resolved incidents found.");
        } else {
            for (String incidentInfo : resolvedIncidents) {
                logger.info(incidentInfo);
            }
        }
    }

    private void handleShutDown() {
        if (dispatchRouter != null) {
            dispatchRouter.shutdown();
        }
    }

    private boolean processCommand(String input, Scanner scanner) {
        switch (input.toLowerCase()) {
            case "status" -> {
                displayStatus();
                return true;
            }
            case "active" -> {
                displayActive();
                return true;
            }
            case "resolve" -> {
                handleResolved(scanner);
                displayResolved();
                return true;
            }
            case "exit" -> {
                handleShutDown();
                return true;
            }
            default -> {
                return false;
            }
        }
    }

    private void processEmergencyAlert(String input, Scanner scanner) {
        String translated = translationService.translateToEnglish(input);
        logger.info("Translated input: {}", translated);

        List<EmergencyDispatch> incidents = triageService.parse(translated);

        for (EmergencyDispatch incident : incidents) {
            if (incident == null) {
                continue;
            }

            processIncidentDetails(incident, scanner);
            dispatchRouter.route(incident);
        }
    }

    private void processIncidentDetails(EmergencyDispatch incident, Scanner scanner) {
        switch (incident.getType()) {
            case FIRE -> {
                logger.info("Are there any hazardous materials? ");
                boolean hazmatInvolved = helper.askTrueOrFalseQuestions(scanner);
                ((FireEmergency) incident).setHazmatInvolved(hazmatInvolved);
            }
            case MEDICAL -> {
                logger.info("How many patients are injured? ");
                int patientCount = helper.askForNumber(scanner);
                ((MedicalEmergency) incident).setPatientCount(patientCount);
            }
            case POLICE -> {
                logger.info("Are there any weapon involved? ");
                boolean weaponInvolved = helper.askTrueOrFalseQuestions(scanner);
                ((PoliceEmergency) incident).setWeaponInvolved(weaponInvolved);
            }
            case COASTAL -> {
                logger.info("Do we have people in distress? ");
                boolean peopleInDistress = helper.askTrueOrFalseQuestions(scanner);
                ((CoastGuardEmergency) incident).setPeopleInDistress(peopleInDistress);
            }
            case UNKNOWN -> {
                logger.info("Is this a prank call? ");
                boolean prankCall = helper.askTrueOrFalseQuestions(scanner);
                ((UnknownEmergency) incident).setPrankCall(prankCall);

                if (prankCall) {
                    incident.setStatus(Status.RESOLVED);
                    logger.info("Prank call detected. Incident marked as RESOLVED");
                } else {
                    incident.setStatus(Status.PENDING);
                }
            }
        }
    }

    public void execution() {
        Scanner scanner = new Scanner(System.in);
        boolean exec = true;
        String input;

        while (exec) {
            logger.info("Please enter your emergency description (or type \"exit\" to quit)");
            logger.info("Or manage incidents using the following keywords: \"status\", \"active\", \"resolve\", \"exit\".");
            logger.info("status: to display all incidents and their respective status");
            logger.info("active: to display all active incidents");
            logger.info("resolve: to set a specific incident from any status to active");
            logger.info("exit: to quit");
            input = scanner.nextLine();

            try {
                if (processCommand(input, scanner)) {
                    if ("exit".equalsIgnoreCase(input)) {
                        exec = false;
                    }
                    continue;
                }
                processEmergencyAlert(input, scanner);
            } catch (Exception e) {
                logger.error("Alert was empty or invalid", e);
            }
        }
        scanner.close();
    }
}
