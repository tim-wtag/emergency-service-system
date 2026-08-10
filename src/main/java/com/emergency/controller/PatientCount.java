package com.emergency.controller;

import java.util.InputMismatchException;
import java.util.Scanner;

public class PatientCount implements IntegerInput{

    @Override
    public int input(Scanner scanner) {
        while (true) {
            try {
                System.out.println("How many patients are injured? ");
                int patientNum = scanner.nextInt();

                return patientNum;
            } catch (InputMismatchException e) {
                System.out.println("Wrong input, it must be a positive number!");
            }
        }
    }

}
