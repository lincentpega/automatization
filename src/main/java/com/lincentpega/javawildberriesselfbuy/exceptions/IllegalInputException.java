package com.lincentpega.javawildberriesselfbuy.exceptions;

public class IllegalInputException extends Exception {
    public IllegalInputException(String message) {
        super(message);
    }

    public IllegalInputException(String message, Throwable cause) {
        super(message, cause);
    }
}
