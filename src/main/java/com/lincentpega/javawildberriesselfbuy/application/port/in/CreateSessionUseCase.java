package com.lincentpega.javawildberriesselfbuy.application.port.in;

public interface CreateSessionUseCase {
    void createSession(String number, String userAgent, String proxy, String resolution);
}