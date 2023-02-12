package com.lincentpega.javawildberriesselfbuy.application.port.in;

import com.lincentpega.javawildberriesselfbuy.application.constants.SessionState;

public interface CheckSessionStateUseCase {
    SessionState getSessionState(Long userId);
}
