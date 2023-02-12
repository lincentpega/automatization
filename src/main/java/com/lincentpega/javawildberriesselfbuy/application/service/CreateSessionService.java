package com.lincentpega.javawildberriesselfbuy.application.service;

import com.lincentpega.javawildberriesselfbuy.application.constants.SessionState;
import com.lincentpega.javawildberriesselfbuy.application.exceptions.SessionDoesntExistException;
import com.lincentpega.javawildberriesselfbuy.application.port.in.CreateSessionUseCase;
import com.lincentpega.javawildberriesselfbuy.application.port.out.SessionRepository;
import com.lincentpega.javawildberriesselfbuy.application.port.out.UserRepository;
import com.lincentpega.javawildberriesselfbuy.domain.DriverSession;
import com.lincentpega.javawildberriesselfbuy.domain.SessionParams;
import com.lincentpega.javawildberriesselfbuy.domain.User;

import java.io.IOException;
import java.util.Optional;

public class CreateSessionService implements CreateSessionUseCase {
    UserRepository userRepository;
    SessionRepository sessionRepository;


    public CreateSessionService(UserRepository userRepository,
                                SessionRepository sessionRepository) {
        this.userRepository = userRepository;
        this.sessionRepository = sessionRepository;
    }

    @Override
    public boolean isSessionPresent(Long userId) {
        return sessionRepository.findById(userId).isPresent();
    }

    @Override
    public void createSession(Long userId, Long number) {
        Optional<User> userOpt = userRepository.findById(userId);
        User user = userOpt.orElseGet(() -> {
            User newUser = new User(userId);
            return userRepository.save(newUser);
        });

        SessionParams userSessionParams = user.getSessionParams();

        DriverSession session = sessionRepository.findById(userId)
                .orElseGet(() -> sessionRepository.save(new DriverSession(userId,
                        userSessionParams.getUserAgent(),
                        userSessionParams.getProxy(),
                        userSessionParams.getResolution())));

        session.start(number);
    }

    @Override
    public void takeScreenshot(Long userId) throws IOException {
        DriverSession session = sessionRepository.findById(userId).orElseThrow(SessionDoesntExistException::new);
        session.takeScreenshot(userId);
    }



    @Override
    public void requestCodeAsSMS(Long userId) {
        DriverSession session = sessionRepository.findById(userId).orElseThrow(SessionDoesntExistException::new);
        session.requestCodeAsSMS();
    }
}