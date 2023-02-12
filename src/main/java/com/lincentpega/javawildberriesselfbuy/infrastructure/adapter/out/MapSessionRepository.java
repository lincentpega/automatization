package com.lincentpega.javawildberriesselfbuy.infrastructure.adapter.out;

import com.lincentpega.javawildberriesselfbuy.application.port.out.SessionRepository;
import com.lincentpega.javawildberriesselfbuy.domain.DriverSession;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
public class MapSessionRepository implements SessionRepository {
    Map<Long, DriverSession> driverSessionMap = new HashMap<>();

    @Override
    public DriverSession save(DriverSession driverSession) {
        driverSessionMap.put(driverSession.getId(), driverSession);
        return driverSessionMap.get(driverSession.getId());
    }

    @Override
    public Optional<DriverSession> findById(Long id) {
        return Optional.ofNullable(driverSessionMap.get(id));
    }

    @Override
    public void delete(Long id) {
        driverSessionMap.remove(id);
    }
}
