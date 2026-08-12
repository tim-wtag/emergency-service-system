package com.emergency.controller;

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

import java.util.Scanner;

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

                EmergencyIncident incident = switch (triageService.parse(translated)) {
                    case FireIncident fireIncident -> {
                        boolean hazmatInvolved = new IsHazmaInvolved().input(scanner);
                        yield new FireIncident(fireIncident.getDescription(), hazmatInvolved);
                    }
                    case MedicalIncident medicalIncident -> {
                        int patientCount = new PatientCount().input(scanner);
                        yield  new MedicalIncident(medicalIncident.getDescription(), patientCount);
                    }
                    case PoliceIncident  policeIncident -> {
                        boolean weaponInvolved = new IsWeaponInvolved().input(scanner);
                        yield  new PoliceIncident(policeIncident.getDescription(), weaponInvolved);
                    }
                    case CoastGuardIncident coastGuardIncident -> {
                        boolean peopleInDistress = new IsPeopleInDistress().input(scanner);
                        yield new CoastGuardIncident(coastGuardIncident.getDescription(), peopleInDistress);
                    }
                    case UnknownIncident unknownIncident -> {
                        boolean prankCall = new IsPrankCall().input(scanner);
                        yield new UnknownIncident(unknownIncident.getDescription(), prankCall);
                    }
                    default -> throw new EmptyAlertException("Invalid input");
                };

                dispatchRouter.route(incident);
                break;

            } catch (EmptyAlertException e) {
                System.out.println(e);
            }

        }
        scanner.close();
    }
}
