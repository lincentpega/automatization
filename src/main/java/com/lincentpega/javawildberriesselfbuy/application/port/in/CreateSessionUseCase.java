package com.lincentpega.javawildberriesselfbuy.application.port.in;

import com.lincentpega.javawildberriesselfbuy.application.constants.SessionState;

import java.io.IOException;

public interface CreateSessionUseCase {
    boolean isSessionPresent(Long userId);

    void createSession(Long userId, Long number);

    void takeScreenshot(Long userId) throws IOException;

    void requestCodeAsSMS(Long userId);
}