package com.lincentpega.javawildberriesselfbuy.exceptions;

public class UserSessionDoesntExist extends Exception {
    public UserSessionDoesntExist() {
    }

    public UserSessionDoesntExist(String message) {
        super(message);
    }

    public UserSessionDoesntExist(String message, Throwable cause) {
        super(message, cause);
    }

    public UserSessionDoesntExist(Throwable cause) {
        super(cause);
    }
}
