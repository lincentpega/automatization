package com.lincentpega.javawildberriesselfbuy.application.port.in;

public interface LogInUseCase {
    void enterNumber(String number);

    void enterCaptcha(String captcha);

    void enterCode(String code);
}
