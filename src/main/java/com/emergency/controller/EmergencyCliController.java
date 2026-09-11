package com.emergency.controller;

import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.emergency.exception.EmptyAlertException;
import com.emergency.model.CoastGuardIncident;
import com.emergency.model.EmergencyIncident;
import com.emergency.model.FireIncident;
import com.emergency.model.IncidentStatus;
import com.emergency.model.IncidentType;
import com.emergency.model.MedicalIncident;
import com.emergency.model.PoliceIncident;
import com.emergency.model.UnknownIncident;
import com.emergency.service.DispatchRouter;
import com.emergency.service.TranslationService;
import com.emergency.service.TriageService;

public class EmergencyCliController {

    private static final Logger logger = LoggerFactory.getLogger(EmergencyCliController.class);
    private final Map<Integer, EmergencyIncident> masterIncidentLog = new ConcurrentHashMap<>();
    private final Map<IncidentType, AtomicInteger> dailyStats = new ConcurrentHashMap<>();
    private TranslationService translationService = null;
    private final TriageService triageService;
    private final DispatchRouter dispatchRouter;
    private final EmergencyInputHelper helper;

    public EmergencyCliController() {
        try {
            translationService = new TranslationService("src/main/resources/dictionary.json");
        } catch (Exception e) {
            e.printStackTrace();
        }
        triageService = new TriageService();
        dispatchRouter = new DispatchRouter();
        helper = new EmergencyInputHelper();

        for (IncidentType type : IncidentType.values()) {
            dailyStats.put(type, new AtomicInteger(0));
        }
    }

    private void displayStatus() {
        for (IncidentType type : IncidentType.values()) {

            long activeCount = masterIncidentLog.values().stream()
                    .filter(i -> i.getType() == type)
                    .filter(i -> i.getStatus() != IncidentStatus.RESOLVED)
                    .count();

            long resolvedCount = masterIncidentLog.values().stream()
                    .filter(i -> i.getType() == type)
                    .filter(i -> i.getStatus() == IncidentStatus.RESOLVED)
                    .count();

            long totalCount = activeCount + resolvedCount;

            logger.info("Type: {} | Total: {} | Active: {} | Resolved: {}",
                    type.name(), totalCount, activeCount, resolvedCount);
        }
    }

    private void recordIncidentStat(IncidentType type) {
        if (type != null && dailyStats.containsKey(type)) {
            dailyStats.get(type).incrementAndGet();
        }
    }

    public void displayActive() {
        logger.info("ACTIVE INCIDENTS: ");
        masterIncidentLog.values().stream().filter(i -> i.getStatus() != IncidentStatus.RESOLVED)
                .forEach(i -> logger.info("Active Incident:[ID: {}], {}", i.getId(), i.getDescription(),
                        i.getType()));
    }

    private void handleResolved(Scanner scanner) {
        while (true) {
            System.out.print("Please enter the ID of the incident to resolve (or type 'exit' to cancel): ");
            String idText = scanner.nextLine().trim();

            if (idText.equalsIgnoreCase("exit")) {
                logger.info("Canceling resolve operation.");
                return;
            }

            if (idText.isEmpty()) {
                logger.info("Error: No ID provided. Please try again.");
                continue;
            }

            try {
                int targetId = Integer.parseInt(idText);
                AtomicBoolean successFlag = new AtomicBoolean(false);

                masterIncidentLog.values().stream()
                        .filter(i -> i.getId() == targetId)
                        .findFirst()
                        .ifPresentOrElse(
                                incident -> {
                                    if (incident.getStatus() == IncidentStatus.RESOLVED) {
                                        logger.info(
                                                "Incident ID {} has already been resolved. Please enter a different ID.",
                                                targetId);
                                    } else {
                                        incident.setStatus(IncidentStatus.RESOLVED);
                                        logger.info("Success: Incident ID {} has been marked as RESOLVED.", targetId);
                                        successFlag.set(true);
                                    }
                                },
                                () -> logger.info("Error: No incident found with ID {}. Please try again.", targetId));

                if (successFlag.get()) {
                    return;
                }

            } catch (NumberFormatException e) {
                logger.info("Invalid format. The ID must be a number.");
            }
        }
    }

    public void displayResolved() {
        logger.info("RESOLVED INCIDENTS: ");
        masterIncidentLog.values().stream()
                .filter(i -> i.getStatus() == IncidentStatus.RESOLVED)
                .forEach(i -> logger.info("Resolved Incident:[ID: {}], {}, {}", 
                        i.getId(), i.getDescription(), i.getType()));
    }

    private void handleShutDown() {
        if (dispatchRouter != null) {
            dispatchRouter.shutdown();
        }
    }

    public void execution() {

        Scanner scanner = new Scanner(System.in);
        boolean exec = true;
        String input;
        String translated = "";
        while (exec) {
            System.out.print("Please enter your emergency description (or type exit to quit): ");
            input = scanner.nextLine();

            try {

                boolean command = true;

                switch (input) {
                    case "status" -> displayStatus();
                    case "active" -> displayActive();
                    case "resolve" -> {
                        handleResolved(scanner);
                        displayResolved();
                    }
                    case "exit" -> {
                        exec = false;
                        handleShutDown();
                    }
                    default -> command = false;
                }

                if (command) {
                    continue;
                }

                translated = translationService.translateToEnglish(input);
                logger.info(translated);

                List<EmergencyIncident> incidents = triageService.parse(translated);
                for (EmergencyIncident incident : incidents) {
                    if (incident == null) {
                        continue;
                    }
                    incident = switch (incident.getType()) {
                        case FIRE -> {
                            logger.info("Are there any hazardous materials? ");
                            boolean hazmatInvolved = helper.askTrueOrFalseQuestions(scanner);
                            ((FireIncident) incident).setHazmatInvolved(hazmatInvolved);
                            yield incident;
                        }
                        case MEDICAL -> {
                            logger.info("How many patients are injured? ");
                            int patientCount = helper.askForNumber(scanner);
                            ((MedicalIncident) incident).setPatientCount(patientCount);
                            yield incident;
                        }
                        case POLICE -> {
                            logger.info("Are there any weapon involved? ");
                            boolean weaponInvolved = helper.askTrueOrFalseQuestions(scanner);
                            ((PoliceIncident) incident).setWeaponInvolved(weaponInvolved);
                            yield incident;
                        }
                        case COASTAL -> {
                            logger.info("Do we have people in distress? ");
                            boolean peopleInDistress = helper.askTrueOrFalseQuestions(scanner);
                            ((CoastGuardIncident) incident).setPeopleInDistress(peopleInDistress);
                            yield incident;
                        }
                        case UNKNOWN -> {
                            logger.info("Is this a prank call? ");
                            boolean prankCall = helper.askTrueOrFalseQuestions(scanner);
                            ((UnknownIncident) incident).setPrankCall(prankCall);
                            if (prankCall) {
                                incident.setStatus(IncidentStatus.RESOLVED);
                                logger.info("Prank call detected. Incident marked as RESOLVED");
                            } else {
                                incident.setStatus(IncidentStatus.PENDING);
                            }
                            yield incident;
                        }
                        default -> incident;
                    };

                    recordIncidentStat(incident.getType());

                    masterIncidentLog.put(incident.getId(), incident);
                    logger.info("masterlog size {}", masterIncidentLog.size());
                    dispatchRouter.route(incident);
                }
            } catch (EmptyAlertException e) {
                logger.error("", e);
            }
            // break;

        }
        scanner.close();
    }
}
