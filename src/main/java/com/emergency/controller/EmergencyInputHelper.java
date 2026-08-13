package com.emergency.controller;

import java.util.InputMismatchException;
import java.util.Scanner;

import com.emergency.exception.NegativeInputException;

public class EmergencyInputHelper {
    public boolean askTrueOrFalseQuestions(Scanner scanner) {
        // what happen if user input yes or no, (y/n)
        while (true) {
            try {
                String inputA = scanner.nextLine();
                boolean inputB = switch (inputA.toLowerCase(null)) {
                    case "yes", "true", "y" -> true;
                    case "no", "false", "n" -> false;
                    default -> throw new InputMismatchException();
                };
                return inputB;
            } catch (InputMismatchException e) {
                scanner.nextLine();
                System.out.println("Wrong input, it can either true or false!");
                System.out.println("Please input again: ");
            }
        }
    }

    public int askForNumber(Scanner scanner) {
        while (true) {
            try {
                int patientNum = scanner.nextInt();
                if(patientNum < 0){
                    throw new NegativeInputException("");
                }

                return patientNum;
            } catch (NegativeInputException e) {
                scanner.nextLine();
                System.out.println("Please input again: ");
            }
            catch(InputMismatchException e){
                scanner.nextLine();
                System.out.println("Must type a number! ");
                System.out.println("Please input again: ");
            }
        }
    }
}
