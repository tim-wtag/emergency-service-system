package com.emergency.exception;

public class NegativeInputException extends RuntimeException{
    public NegativeInputException(String message){
        System.out.println("Wrong input as it cannot be negative, it must be a positive number!");
    }
}
