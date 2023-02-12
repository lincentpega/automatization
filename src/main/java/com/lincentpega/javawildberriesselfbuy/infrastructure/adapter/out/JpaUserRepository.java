package com.lincentpega.javawildberriesselfbuy.infrastructure.adapter.out;

import com.lincentpega.javawildberriesselfbuy.application.port.out.UserRepository;
import com.lincentpega.javawildberriesselfbuy.domain.User;
import com.lincentpega.javawildberriesselfbuy.infrastructure.mapper.UserToUserEntityMapper;
import com.lincentpega.javawildberriesselfbuy.infrastructure.adapter.out.entity.UserEntity;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class JpaUserRepository implements UserRepository {
    private final JpaUserEntityRepository jpaUserEntityRepository;
    private final UserToUserEntityMapper userToUserEntityMapper;

    public JpaUserRepository(JpaUserEntityRepository jpaUserEntityRepository,
                             UserToUserEntityMapper userToUserEntityMapper) {
        this.jpaUserEntityRepository = jpaUserEntityRepository;
        this.userToUserEntityMapper = userToUserEntityMapper;
    }

    @Override
    public User save(User user) {
        UserEntity userEntity = userToUserEntityMapper.userToUserEntity(user);
        UserEntity savedUserEntity = jpaUserEntityRepository.save(userEntity);
        return userToUserEntityMapper.userEntityToUser(savedUserEntity);
    }

    @Override
    public Optional<User> findById(Long id) {
        Optional<UserEntity> userEntityOptional = jpaUserEntityRepository.findById(id);
        return userEntityOptional.map(userToUserEntityMapper::userEntityToUser);
    }
}
