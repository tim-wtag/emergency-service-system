package com.emergency.exception;

public class NegativeInputException extends RuntimeException{
    public NegativeInputException(String message){
        super(message);
    }
}
