package com.emergency.controller;

import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;
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
    private final TranslationService translationService;
    private final TriageService triageService;
    private final DispatchRouter dispatchRouter;
    private final EmergencyInputHelper helper;

    public EmergencyCliController() throws Exception {
        translationService = new TranslationService("src/main/resources/dictionary.json");
        triageService = new TriageService();
        dispatchRouter = new DispatchRouter();
        helper = new EmergencyInputHelper();

        for (IncidentType type : IncidentType.values()) {
            dailyStats.put(type, new AtomicInteger(0));
        }
    }

    public void displayStatus() {
        dailyStats.forEach((key, count) -> logger.info("Status {} {} ", key, count.get()));
    }

    private void recordIncicentStat(IncidentType type) {
        if (type != null && dailyStats.containsKey(type)) {
            dailyStats.get(type).incrementAndGet();
        }
    }

    public void displayActive() {
        logger.info("ACTIVE INCIDENTS: ");
        masterIncidentLog.values().stream().filter(i -> i.getStatus() != IncidentStatus.RESOLVED)
                .forEach(i -> logger.info("Active Incident:[ID: {}], {}, {}", i.getId(), i.getDescription(), i.getType()));
    }

    public void handleResolved(String input) {
        try {
            int targetId = Integer.parseInt(input.substring(8).trim());
            EmergencyIncident incident = masterIncidentLog.get(targetId);
            
            if (incident != null) {
                incident.setStatus(IncidentStatus.RESOLVED);
                logger.info("Incident {} marked as RESOLVED.", targetId);
            } else {
                System.out.println("Incident ID " + targetId + " not found.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid resolve command. Use 'resolve <id>'.");
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

                switch (input) {
                    case "status" -> displayStatus();
                    case "active" -> displayActive();
                    case "resolve" -> handleResolved(input);
                    case "exit" -> {
                        exec = false;
                        // handleShutDown();
                    }
                    default -> translated = translationService.translateToEnglish(input);
                }

                logger.info(translated);



                EmergencyIncident[] incidents = triageService.parse(translated);
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
                            yield incident;
                        }
                        default -> incident;
                    }; 

                    masterIncidentLog.put(incident.getId(), incident);

                    dailyStats.get(incident.getType()).incrementAndGet();

                    dispatchRouter.route(incident);
                }
            } catch (EmptyAlertException e) {
                logger.error("", e);
            }
            break;

        }
        scanner.close();
    }
}
