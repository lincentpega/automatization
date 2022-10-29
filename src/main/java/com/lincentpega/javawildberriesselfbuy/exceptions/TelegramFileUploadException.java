package com.lincentpega.javawildberriesselfbuy.exceptions;

public class TelegramFileUploadException extends Exception{
    public TelegramFileUploadException() {
    }

    public TelegramFileUploadException(String message) {
        super(message);
    }

    public TelegramFileUploadException(String message, Throwable cause) {
        super(message, cause);
    }
}
