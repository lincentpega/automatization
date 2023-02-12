package com.lincentpega.javawildberriesselfbuy.application.service;

import com.lincentpega.javawildberriesselfbuy.application.exceptions.SessionDoesntExistException;
import com.lincentpega.javawildberriesselfbuy.application.exceptions.UserDoesntExistException;
import com.lincentpega.javawildberriesselfbuy.application.port.in.LogInUseCase;
import com.lincentpega.javawildberriesselfbuy.application.port.out.SessionRepository;
import com.lincentpega.javawildberriesselfbuy.application.port.out.UserRepository;
import com.lincentpega.javawildberriesselfbuy.domain.DriverSession;
import com.lincentpega.javawildberriesselfbuy.domain.SessionInfo;
import com.lincentpega.javawildberriesselfbuy.domain.User;
import org.openqa.selenium.Cookie;

import java.util.List;
import java.util.Set;

public class LogInService implements LogInUseCase {
    private final SessionRepository sessionRepository;
    private final UserRepository userRepository;

    public LogInService(SessionRepository sessionRepository,
                        UserRepository userRepository) {
        this.sessionRepository = sessionRepository;
        this.userRepository = userRepository;
    }

    @Override
    public void enterCaptcha(Long userId, String captcha) {
        DriverSession session = sessionRepository.findById(userId).orElseThrow(SessionDoesntExistException::new);
        session.enterCaptcha(captcha);
    }

    @Override
    public void enterCode(Long userId, String code) {
        DriverSession session = sessionRepository.findById(userId).orElseThrow(SessionDoesntExistException::new);
        session.enterCode(code);
    }

    @Override
    public void saveCookies(Long userId) {
        DriverSession session = sessionRepository.findById(userId).orElseThrow(SessionDoesntExistException::new);
        User user = userRepository.findById(userId).orElseThrow(UserDoesntExistException::new);

        List<SessionInfo> userSessionInfoList = user.getSessionInfoList();
        Set<Cookie> cookies = session.getSessionCookies();
        userSessionInfoList.setCookies(cookies);
        user.setSessionInfoList(userSessionInfoList);
        userRepository.save(user);
    }
}
