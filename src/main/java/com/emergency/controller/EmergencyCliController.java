package com.emergency.controller;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Scanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.emergency.model.CoastGuardEmergency;
import com.emergency.model.EmergencyDispatch;
import com.emergency.model.FireEmergency;
import com.emergency.model.MedicalEmergency;
import com.emergency.model.PoliceEmergency;
import com.emergency.model.Status;
import com.emergency.model.UnknownEmergency;
import com.emergency.repository.EmergencyRepository;
import com.emergency.repository.IncidentRepository;
import com.emergency.service.DispatchRouter;
import com.emergency.service.TranslationService;
import com.emergency.service.TriageService;

public class EmergencyCliController {

    private static final Logger logger = LoggerFactory.getLogger(EmergencyCliController.class);

    private static final String STATUS_PENDING = "pending";
    private static final String STATUS_DISPATCHED = "dispatched";
    private static final String STATUS_RESOLVED = "resolved";

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
            logger.error("Failed to load translation service", e);
        }

        incidentRepository = new IncidentRepository();
        emergencyRepository = new EmergencyRepository();
        triageService = new TriageService(incidentRepository, emergencyRepository);
        dispatchRouter = new DispatchRouter();
        helper = new EmergencyInputHelper();
    }

    private void displayIncidentStatus() {
        Map<String, Integer> incidentCounts = incidentRepository.getIncidentStatusCounts();
        int pendingIncidents = incidentCounts.getOrDefault(STATUS_PENDING, 0);
        int dispatchedIncidents = incidentCounts.getOrDefault(STATUS_DISPATCHED, 0);
        int resolvedIncidents = incidentCounts.getOrDefault(STATUS_RESOLVED, 0);
        int activeIncidents = pendingIncidents + dispatchedIncidents;

        logger.info("--- PARENT INCIDENTS ---");
        logger.info("Total   : {}", activeIncidents + resolvedIncidents);
        logger.info("Active  : {} (Pending: {}, Dispatched: {})", activeIncidents, pendingIncidents, dispatchedIncidents);
        logger.info("Resolved: {}", resolvedIncidents);
    }

    private void displayEmergencyStatus() {
        Map<String, Integer> emergencyCounts = emergencyRepository.getEmergencyStatusCounts();
        int pendingEmergencies = emergencyCounts.getOrDefault(STATUS_PENDING, 0);
        int dispatchedEmergencies = emergencyCounts.getOrDefault(STATUS_DISPATCHED, 0);
        int resolvedEmergencies = emergencyCounts.getOrDefault(STATUS_RESOLVED, 0);
        int activeEmergencies = pendingEmergencies + dispatchedEmergencies;

        logger.info("--- EMERGENCY UNITS ---");
        logger.info("Total   : {}", activeEmergencies + resolvedEmergencies);
        logger.info("Active  : {} (Pending: {}, Dispatched: {})", activeEmergencies, pendingEmergencies, dispatchedEmergencies);
        logger.info("Resolved: {}", resolvedEmergencies);
    }

    public void displayActive() {
        logger.info("--- ACTIVE PARENT INCIDENTS ---");
        List<String> activeIncidents = incidentRepository.getActiveIncidentsFormatted();
        if (activeIncidents.isEmpty()) {
            logger.info("No active parent incidents found.");
        } else {
            for (String incidentInfo : activeIncidents) {
                logger.info(incidentInfo);
            }
        }

        logger.info("--- ACTIVE EMERGENCY UNITS ---");
        List<EmergencyDispatch> pendings = emergencyRepository.getPendingEmergencies();
        List<EmergencyDispatch> dispatched = emergencyRepository.getDispatchedEmergencies();

        if (pendings.isEmpty() && dispatched.isEmpty()) {
            logger.info("No active emergency units found.");
        } else {
            for (EmergencyDispatch e : pendings) {
                logger.info("PENDING Unit    [ID: {}] | Desc: {}", e.getId(), e.getComment());
            }
            for (EmergencyDispatch e : dispatched) {
                logger.info("DISPATCHED Unit [ID: {}] | Desc: {}", e.getId(), e.getComment());
            }
        }
    }

    public void displayResolved() {
        logger.info("--- RESOLVED PARENT INCIDENTS ---");
        List<String> resolvedIncidents = incidentRepository.getResolvedIncidentsFormatted();

        if (resolvedIncidents.isEmpty()) {
            logger.info("No resolved parent incidents found.");
        } else {
            for (String incidentInfo : resolvedIncidents) {
                logger.info(incidentInfo);
            }
        }

        logger.info("--- RESOLVED EMERGENCY UNITS ---");
        List<EmergencyDispatch> resolvedEmergencies = emergencyRepository.getResolvedEmergencies();

        if (resolvedEmergencies.isEmpty()) {
            logger.info("No resolved emergency units found.");
        } else {
            for (EmergencyDispatch e : resolvedEmergencies) {
                logger.info("RESOLVED Unit   [ID: {}] | Desc: {}", e.getId(), e.getComment());
            }
        }
    }

    public void displayByDepartment() {
        logger.info("--- EMERGENCIES BY DEPARTMENT ---");

        List<EmergencyDispatch> fire = emergencyRepository.getFireEmergencies();
        List<EmergencyDispatch> medical = emergencyRepository.getMedicalEmergencies();
        List<EmergencyDispatch> police = emergencyRepository.getPoliceEmergencies();
        List<EmergencyDispatch> coastal = emergencyRepository.getCoastGuardEmergencies();
        List<EmergencyDispatch> unknown = emergencyRepository.getUnknownEmergencies();

        logger.info("FIRE DEPT ({} units):", fire.size());
        fire.forEach(e -> logger.info("  -> [ID: {}] Status: {}", e.getId(), e.getStatus()));

        logger.info("MEDICAL ({} units):", medical.size());
        medical.forEach(e -> logger.info("  -> [ID: {}] Status: {}", e.getId(), e.getStatus()));

        logger.info("POLICE ({} units):", police.size());
        police.forEach(e -> logger.info("  -> [ID: {}] Status: {}", e.getId(), e.getStatus()));

        logger.info("COAST GUARD ({} units):", coastal.size());
        coastal.forEach(e -> logger.info("  -> [ID: {}] Status: {}", e.getId(), e.getStatus()));

        if (!unknown.isEmpty()) {
            logger.info("UNKNOWN ({} units):", unknown.size());
            unknown.forEach(e -> logger.info("  -> [ID: {}] Status: {}", e.getId(), e.getStatus()));
        }
    }

    private void handleDispatched(Scanner scanner) {
        boolean active = true;
        while (active) {
            logger.info("Please enter the ID of the PENDING EMERGENCY UNIT to dispatch (or type 'exit' to cancel): ");
            if (!scanner.hasNextLine()) {
                break;
            }
            String idText = scanner.nextLine().trim();

            if (idText.equalsIgnoreCase("exit")) {
                logger.info("Canceling dispatch operation.");
                active = false;
            } else if (idText.isEmpty()) {
                logger.info("Error: No ID provided. Please try again.");
            } else {
                active = processDispatchAttempt(idText);
            }
        }
    }

    private boolean processDispatchAttempt(String idText) {
        try {
            java.util.UUID targetId = java.util.UUID.fromString(idText);

            if (emergencyRepository.markEmergencyAsDispatched(targetId)) {
                logger.info("Success: Emergency unit {} has been marked as DISPATCHED.", targetId);
                return false;
            }

            logger.info("Error: Could not dispatch unit. Ensure the ID is correct and active.");
            return true;

        } catch (IllegalArgumentException e) {
            logger.info("Invalid format. The ID must be a valid UUID.", e);
            return true;
        }
    }

    private void handleResolved(Scanner scanner) {
        boolean active = true;
        while (active) {
            logger.info("Please enter the ID of the EMERGENCY UNIT to resolve (or type 'exit' to cancel): ");
            if (!scanner.hasNextLine()) {
                break;
            }
            String idText = scanner.nextLine().trim();

            if (idText.equalsIgnoreCase("exit")) {
                logger.info("Canceling resolve operation.");
                active = false;
            } else if (idText.isEmpty()) {
                logger.info("Error: No ID provided. Please try again.");
            } else {
                active = processResolveAttempt(idText);
            }
        }
    }

    private boolean processResolveAttempt(String idText) {
        try {
            java.util.UUID targetId = java.util.UUID.fromString(idText);
            java.util.UUID parentIncidentId = emergencyRepository.markEmergencyAsResolved(targetId);

            if (parentIncidentId == null) {
                logger.info("Error: No active emergency found with ID {}. It may be invalid or already resolved.", targetId);
                return true;
            }

            logger.info("Success: Emergency unit {} has been marked as RESOLVED.", targetId);
            incidentRepository.syncIncidentStatus(parentIncidentId);
            String newParentStatus = incidentRepository.getIncidentStatus(parentIncidentId);

            if (STATUS_RESOLVED.equalsIgnoreCase(newParentStatus)) {
                logger.info("All units finished. Parent Incident {} is now OFFICIALLY RESOLVED.", parentIncidentId);
            } else if (logger.isInfoEnabled()) {
                logger.info("Parent Incident {} remains open (Status: {}).", parentIncidentId, newParentStatus.toUpperCase());
            }

            return false;

        } catch (IllegalArgumentException e) {
            logger.info("Invalid format. The ID must be a valid UUID.", e);
            return true;
        }
    }

    private void handleShutDown() {
        // if (dispatchRouter == null) {
        //     dispatchRouter.shutdown();
        // }
        dispatchRouter.shutdown();
    }

    private boolean processCommand(String input, Scanner scanner) {
        switch (input.toLowerCase()) {
            case "emergency status" -> {
                displayEmergencyStatus();
                return true;
            }
            case "incident status" -> {
                displayIncidentStatus();
                return true;
            }
            case "active" -> {
                displayActive();
                return true;
            }
            case "resolved" -> {
                displayResolved();
                return true;
            }
            case "departments" -> {
                displayByDepartment();
                return true;
            }
            case "dispatch" -> {
                handleDispatched(scanner);
                return true;
            }
            case "resolve" -> {
                handleResolved(scanner);
                return true;
            }
            case "exit" -> {
                logger.info("before");
                handleShutDown();
                logger.info("after");
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

        List<EmergencyDispatch> emergencies = triageService.parseAndSave(translated);

        if (emergencies == null || emergencies.isEmpty()) {
            logger.warn("System prevented creation of an empty incident. No emergency units generated.");
            return;
        }

        for (EmergencyDispatch emergency : emergencies) {
            if (emergency == null) continue;
            processIncidentDetails(emergency, scanner);
        }

        for (EmergencyDispatch emergency : emergencies) {
            if (emergency == null) continue;
            dispatchRouter.routeAsync(emergency);
        }
    }

    private void processIncidentDetails(EmergencyDispatch incident, Scanner scanner) {
        switch (incident.getType()) {
            case FIRE -> {
                logger.info("Are there any hazardous materials? ");
                ((FireEmergency) incident).setHazmatInvolved(helper.askTrueOrFalseQuestions(scanner));
            }
            case MEDICAL -> {
                logger.info("How many patients are injured? ");
                ((MedicalEmergency) incident).setPatientCount(helper.askForNumber(scanner));
            }
            case POLICE -> {
                logger.info("Are there any weapon involved? ");
                ((PoliceEmergency) incident).setWeaponInvolved(helper.askTrueOrFalseQuestions(scanner));
            }
            case COASTAL -> {
                logger.info("Do we have people in distress? ");
                ((CoastGuardEmergency) incident).setPeopleInDistress(helper.askTrueOrFalseQuestions(scanner));
            }
            case UNKNOWN -> {
                logger.info("Is this a prank call? ");
                boolean prankCall = helper.askTrueOrFalseQuestions(scanner);
                ((UnknownEmergency) incident).setPrankCall(prankCall);

                if (prankCall) {
                    incident.setStatus(Status.RESOLVED);
                    emergencyRepository.markEmergencyAsResolved(incident.getId());
                    if (incident.getParentIncident() != null) {
                        incidentRepository.syncIncidentStatus(incident.getParentIncident().getId());
                    }
                    logger.info("Prank call detected. Incident marked as RESOLVED in the database.");
                } else {
                    incident.setStatus(Status.PENDING);
                }
            }
        }
    }

    public void execution() {
        Scanner scanner = new Scanner(System.in);
        boolean active = true;

        while (active) {
            logger.info("\n=== EMERGENCY DISPATCH SYSTEM ===");
            logger.info("Enter an emergency description OR use a command:");
            logger.info("VIEWS  : 'emergency status', 'incident status', 'active', 'resolved', 'departments'");
            logger.info("ACTIONS: 'dispatch', 'resolve'");
            logger.info("SYSTEM : 'exit'");

            if (!scanner.hasNextLine()) {
                handleShutDown();
                active = false;
            } else {
                String input = scanner.nextLine().trim();

                try {
                    if ("exit".equalsIgnoreCase(input)) {
                        logger.info("Shutting down Emergency Dispatch System...");
                        handleShutDown();
                        active = false;
                    } else if (!processCommand(input, scanner)) {
                        processEmergencyAlert(input, scanner);
                    }
                } catch (NoSuchElementException e) {
                    logger.info("Input stream ended. Exiting system...", e);
                    handleShutDown();
                    active = false;
                } catch (Exception e) {
                    logger.error("Alert was empty or invalid", e);
                }
            }
        }
        scanner.close();
    }
}