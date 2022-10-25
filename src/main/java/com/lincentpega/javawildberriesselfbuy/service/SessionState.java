package com.lincentpega.javawildberriesselfbuy.service;

public enum SessionState {
    SESSION_STARTED,
    SITE_ENTERED,
    NUMBER_ENTERED,
    CAPTCHA_APPEARED,
    CAPTCHA_CODE_WRONG,
    PUSH_UP_REQUESTED,
    SMS_REQUESTED,
    AUTHENTICATED,
    SESSION_CLOSED
}
