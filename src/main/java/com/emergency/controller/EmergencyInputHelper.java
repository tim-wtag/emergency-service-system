package com.emergency.controller;

import java.util.InputMismatchException;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.emergency.exception.NegativeInputException;

public class EmergencyInputHelper {

    private static final Logger logger = LoggerFactory.getLogger(EmergencyInputHelper.class);

    public boolean askTrueOrFalseQuestions(Scanner scanner) {
        while (true) {
            try {
                String inputA = scanner.nextLine();
                return switch (inputA.toLowerCase()) {
                    case "yes", "true", "y" -> true;
                    case "no", "false", "n" -> false;
                    default -> throw new InputMismatchException();
                };
            } catch (InputMismatchException e) {
                scanner.nextLine();
                logger.warn("Wrong input, it can either true or false!");
                logger.warn("Please input again: ");
            }
        }
    }

    public int askForNumber(Scanner scanner) {
        while (true) {
            try {
                int patientNum = scanner.nextInt();
                if (patientNum < 0) {
                    throw new NegativeInputException("");
                }

                return patientNum;
            } catch (NegativeInputException e) {
                scanner.nextLine();
                logger.warn("Wrong input as it cannot be negative, it must be a positive number!");
                logger.warn("Please input again: ");
            } catch (InputMismatchException e) {
                scanner.nextLine();
                logger.warn("Must type a number! ");
                logger.warn("Please input again: ");
            }
        }
    }
}
