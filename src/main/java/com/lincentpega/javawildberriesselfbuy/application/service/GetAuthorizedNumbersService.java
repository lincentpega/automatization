package com.lincentpega.javawildberriesselfbuy.application.service;

import com.lincentpega.javawildberriesselfbuy.application.exceptions.UserDoesntExistException;
import com.lincentpega.javawildberriesselfbuy.application.port.in.GetAuthorizedNumbersUseCase;
import com.lincentpega.javawildberriesselfbuy.application.port.out.UserRepository;
import com.lincentpega.javawildberriesselfbuy.domain.SessionInfo;
import com.lincentpega.javawildberriesselfbuy.domain.User;

import java.util.List;
import java.util.stream.Collectors;

public class GetAuthorizedNumbersService implements GetAuthorizedNumbersUseCase {
    UserRepository userRepository;

    public GetAuthorizedNumbersService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<String> getAuthorizedNumbers(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(UserDoesntExistException::new);
        List<SessionInfo> sessionCookies = user.getSessionInfoList();
        return sessionCookies
                .stream()
                .map(SessionInfo::getNumber)
                .map(el -> Long.toString(el))
                .collect(Collectors.toList());
    }
}
