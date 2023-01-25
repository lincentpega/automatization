package com.lincentpega.javawildberriesselfbuy.infrastructure;

import com.lincentpega.javawildberriesselfbuy.domain.User;
import com.lincentpega.javawildberriesselfbuy.infrastructure.adapter.out.UserEntity;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface UserToUserEntityMapper {
    User userEntityToUser(UserEntity userEntity);

    UserEntity userToUserEntity(User user);
}
