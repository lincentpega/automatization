package com.lincentpega.javawildberriesselfbuy.constants;

public enum BotMessageEnum {
    UNKNOWN_EXCEPTION("Что-то полшло не так, обратитесь к разработчику"),
    SESSION_DOESNT_EXIST("Сессии ещё/уже не существует, введите /start"),
    SESSION_ALREADY_EXISTS("Сессия уже существует"),
    SESSION_CREATED("Сессия создана, вход на Wildberries выполнен");

    private final String message;

    BotMessageEnum(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
