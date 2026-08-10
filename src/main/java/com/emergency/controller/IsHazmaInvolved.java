package com.emergency.controller;

import java.util.InputMismatchException;
import java.util.Scanner;

public class IsHazmaInvolved implements BooleanInput {

    @Override
    public boolean input(Scanner scanner) {
        while (true) {
            try {
                System.out.println("Is hazmat involved? (true/false)");
                boolean hazmat = scanner.nextBoolean();
                
                return hazmat;
            } catch (InputMismatchException e) {
                scanner.nextLine();
                System.out.println("Wrong input, it can either true or false!");
            }
        }
    }

}
