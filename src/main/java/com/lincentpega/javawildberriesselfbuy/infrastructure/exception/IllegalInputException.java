package com.lincentpega.javawildberriesselfbuy.infrastructure.exception;

public class IllegalInputException extends Exception {
    public IllegalInputException(String message) {
        super(message);
    }
    public IllegalInputException(String message, Throwable cause) {
        super(message, cause);
    }

    public IllegalInputException() {

    }
}
