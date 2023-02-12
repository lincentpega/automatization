package com.lincentpega.javawildberriesselfbuy.application.service;

import com.lincentpega.javawildberriesselfbuy.application.exceptions.SessionDoesntExistException;
import com.lincentpega.javawildberriesselfbuy.application.port.in.CloseSessionUseCase;
import com.lincentpega.javawildberriesselfbuy.application.port.out.SessionRepository;
import com.lincentpega.javawildberriesselfbuy.domain.DriverSession;

public class CloseSessionService implements CloseSessionUseCase {
    SessionRepository sessionRepository;

    public CloseSessionService(SessionRepository sessionRepository) {
        this.sessionRepository = sessionRepository;
    }

    @Override
    public void closeSession(Long userId) {
        DriverSession session = sessionRepository.findById(userId).orElseThrow(SessionDoesntExistException::new);
        session.close();
        sessionRepository.delete(userId);
    }
}