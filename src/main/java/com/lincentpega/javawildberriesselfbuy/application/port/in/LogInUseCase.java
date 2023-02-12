package com.lincentpega.javawildberriesselfbuy.application.port.in;

public interface LogInUseCase {
    void enterCaptcha(Long userId, String captcha);

    void enterCode(Long userId, String code);

    void saveCookies(Long userId);
}