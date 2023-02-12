package com.lincentpega.javawildberriesselfbuy.application.service;

import com.lincentpega.javawildberriesselfbuy.application.constants.SessionState;
import com.lincentpega.javawildberriesselfbuy.application.exceptions.SessionDoesntExistException;
import com.lincentpega.javawildberriesselfbuy.application.port.in.CheckSessionStateUseCase;
import com.lincentpega.javawildberriesselfbuy.application.port.out.SessionRepository;
import com.lincentpega.javawildberriesselfbuy.domain.DriverSession;

public class CheckSessionStateService implements CheckSessionStateUseCase {
    private final SessionRepository sessionRepository;

    public CheckSessionStateService(SessionRepository sessionRepository) {
        this.sessionRepository = sessionRepository;
    }

    @Override
    public SessionState getSessionState(Long userId) {
        DriverSession session = sessionRepository.findById(userId).orElseThrow(SessionDoesntExistException::new);
        return session.getState();
    }
}
