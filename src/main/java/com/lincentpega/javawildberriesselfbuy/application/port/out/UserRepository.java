package com.lincentpega.javawildberriesselfbuy.application.port.out;

import com.lincentpega.javawildberriesselfbuy.domain.User;

import java.util.Optional;

public interface UserRepository {
    User save(User user);

    Optional<User> findById(Long id);
}
