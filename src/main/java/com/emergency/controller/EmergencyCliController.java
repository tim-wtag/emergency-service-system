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
                    boolean hazmatInvolved = new IsHazmaInvolved().input(scanner);
                    incident = new FireIncident(incident.getDescription(), hazmatInvolved);
                } else if (incident instanceof MedicalIncident) {
                    int patientCount = new PatientCount().input(scanner);
                    incident = new MedicalIncident(incident.getDescription(), patientCount);
                } else if (incident instanceof PoliceIncident) {
                    boolean weaponInvolved = new IsWeaponInvolved().input(scanner);
                    incident = new PoliceIncident(incident.getDescription(), weaponInvolved);
                } else if (incident instanceof CoastGuardIncident) {
                    boolean peopleInDistress = new IsPeopleInDistress().input(scanner);
                    incident = new CoastGuardIncident(incident.getDescription(), peopleInDistress);
                } else if (incident instanceof UnknownIncident) {
                    boolean prankCall = new IsPrankCall().input(scanner);
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
