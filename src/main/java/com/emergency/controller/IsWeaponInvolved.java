package com.emergency.controller;

import java.util.InputMismatchException;
import java.util.Scanner;

public class IsWeaponInvolved implements BooleanInput{

    @Override
    public boolean input(Scanner scanner) {
        while (true) {
            try {
                System.out.println("Is weapon involved? (true/false)");
                boolean weapon = scanner.nextBoolean();

                return weapon;
            } catch (InputMismatchException e) {
                scanner.nextLine();
                System.out.println("Wrong input, it can either true or false!");
            }
        }
    }

}
