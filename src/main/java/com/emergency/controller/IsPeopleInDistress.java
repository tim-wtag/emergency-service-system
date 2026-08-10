package com.emergency.controller;

import java.util.InputMismatchException;
import java.util.Scanner;

public class IsPeopleInDistress implements BooleanInput{

    @Override
    public boolean input(Scanner scanner) {
        while (true) {
            try {
                System.out.println("Are people in distress? (true/false)");
                boolean distress = scanner.nextBoolean();

                return distress;
            } catch (InputMismatchException e) {
                scanner.nextLine();
                System.out.println("Wrong input, it can either true or false!");
            }
        }
    }

}
