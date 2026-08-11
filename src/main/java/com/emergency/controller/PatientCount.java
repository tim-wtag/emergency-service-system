package com.emergency.controller;

import java.util.InputMismatchException;
import java.util.Scanner;

import com.emergency.exception.NegativeInputException;

public class PatientCount implements IntegerInput{

    @Override
    public int input(Scanner scanner) {
        while (true) {
            try {
                System.out.println("How many patients are injured? ");
                int patientNum = scanner.nextInt();
                if(patientNum < 0){
                    throw new InputMismatchException();
                }

                return patientNum;
            } catch (NegativeInputException e) {
                scanner.nextLine();
                System.out.println(e);
            }
        }
    }

}
