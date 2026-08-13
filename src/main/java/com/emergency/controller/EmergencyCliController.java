package com.emergency.controller;

import java.util.InputMismatchException;
import java.util.Scanner;

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

    private final TranslationService translationService;
    private final TriageService triageService;
    private final DispatchRouter dispatchRouter;

    public EmergencyCliController() {
        translationService = new TranslationService();
        triageService = new TriageService();
        dispatchRouter = new DispatchRouter();
    }

    public void execution() {

        Scanner scanner = new Scanner(System.in);
        String input;
        while (true) {
            System.out.println("Please enter your emergency description (or type exit to quit): ");
            input = scanner.nextLine();

            try {
                if (input.equalsIgnoreCase("exit")) {
                    System.out.println("Goodbye, have a nice day!");
                    break;
                }

                String translated = translationService.translateToEnglish(input);
                System.out.println(translated);

                EmergencyIncident incident = triageService.parse(translated);

                if (incident instanceof FireIncident) {
                    System.out.println("Are there any hazardous materials? ");
                    boolean hazmatInvolved = new EmergencyInputHelper().askTrueOrFalseQuestions(scanner);
                    incident = new FireIncident(incident.getDescription(), hazmatInvolved);
                } else if (incident instanceof MedicalIncident) {
                    System.out.println("How many patients are injured? ");
                    int patientCount = new EmergencyInputHelper().askForNumber(scanner);
                    incident = new MedicalIncident(incident.getDescription(), patientCount);
                } else if (incident instanceof PoliceIncident) {
                    System.out.println("Are there any weapon involved? ");
                    boolean weaponInvolved = new EmergencyInputHelper().askTrueOrFalseQuestions(scanner);
                    incident = new PoliceIncident(incident.getDescription(), weaponInvolved);
                } else if (incident instanceof CoastGuardIncident) {
                    System.out.println("Do we have people in distress? ");
                    boolean peopleInDistress = new EmergencyInputHelper().askTrueOrFalseQuestions(scanner);
                    incident = new CoastGuardIncident(incident.getDescription(), peopleInDistress);
                } else if (incident instanceof UnknownIncident) {
                    System.out.println("Is this a prank call? ");
                    boolean prankCall = new EmergencyInputHelper().askTrueOrFalseQuestions(scanner);
                    incident = new UnknownIncident(incident.getDescription(), prankCall);
                }

                dispatchRouter.route(incident);

                break;

            } catch (EmptyAlertException e) {
                System.out.println(e);
            } 

        }
        scanner.close();
    }
}
