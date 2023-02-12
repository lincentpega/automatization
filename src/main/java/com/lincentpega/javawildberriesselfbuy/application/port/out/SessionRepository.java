package com.lincentpega.javawildberriesselfbuy.application.port.out;

import com.lincentpega.javawildberriesselfbuy.domain.DriverSession;

import java.util.Optional;

public interface SessionRepository {
    DriverSession save(DriverSession driverSession);

    Optional<DriverSession> findById(Long id);

    void delete(Long userId);
}