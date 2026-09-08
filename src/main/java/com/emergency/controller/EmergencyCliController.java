package com.emergency.controller;

import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.emergency.exception.EmptyAlertException;
import com.emergency.model.CoastGuardIncident;
import com.emergency.model.EmergencyIncident;
import com.emergency.model.FireIncident;
import com.emergency.model.MedicalIncident;
import com.emergency.model.PoliceIncident;
import com.emergency.model.UnknownIncident;
import com.emergency.service.DispatchRouter;
import com.emergency.service.TranslationService;
import com.emergency.service.TriageService;

public class EmergencyCliController {

    private static final Logger logger = LoggerFactory.getLogger(EmergencyCliController.class);
    private final TranslationService translationService;
    private final TriageService triageService;
    private final DispatchRouter dispatchRouter;
    private final EmergencyInputHelper helper;

    public EmergencyCliController() {
        translationService = new TranslationService();
        triageService = new TriageService();
        dispatchRouter = new DispatchRouter();
        helper = new EmergencyInputHelper();
    }

    public void execution() {

        Scanner scanner = new Scanner(System.in);
        String input;
        while (true) {
            logger.info("Please enter your emergency description (or type exit to quit): ");
            input = scanner.nextLine();

            try {
                if (input.equalsIgnoreCase("exit")) {
                    logger.info("Goodbye, have a nice day!");
                    break;
                }

                String translated = translationService.translateToEnglish(input);
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
                            yield new FireIncident(incident.getDescription(), hazmatInvolved);
                        }
                        case MEDICAL -> {
                            logger.info("How many patients are injured? ");
                            int patientCount = helper.askForNumber(scanner);
                            yield new MedicalIncident(incident.getDescription(), patientCount);
                        }
                        case POLICE -> {
                            logger.info("Are there any weapon involved? ");
                            boolean weaponInvolved = helper.askTrueOrFalseQuestions(scanner);
                            yield new PoliceIncident(incident.getDescription(), weaponInvolved);
                        }
                        case COASTAL -> {
                            logger.info("Do we have people in distress? ");
                            boolean peopleInDistress = helper.askTrueOrFalseQuestions(scanner);
                            yield new CoastGuardIncident(incident.getDescription(), peopleInDistress);
                        }
                        case UNKNOWN -> {
                            logger.info("Is this a prank call? ");
                            boolean prankCall = helper.askTrueOrFalseQuestions(scanner);
                            yield new UnknownIncident(incident.getDescription(), prankCall);
                        }
                    };

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
