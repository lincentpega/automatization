package com.lincentpega.javawildberriesselfbuy.infrastructure.mapper;

import com.lincentpega.javawildberriesselfbuy.infrastructure.adapter.out.entity.CookieEntity;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.openqa.selenium.Cookie;

@Mapper(componentModel = "spring",
        injectionStrategy = InjectionStrategy.CONSTRUCTOR)

public abstract class CookieToCookieEntityMapper {
    @Mapping(target = "id", ignore = true)
    abstract CookieEntity cookieToCookieEntity(Cookie cookie);

    Cookie cookieEntityToCookie(CookieEntity cookieEntity) {
        return new Cookie(cookieEntity.getName(),
                cookieEntity.getValue(),
                cookieEntity.getDomain(),
                cookieEntity.getPath(),
                cookieEntity.getExpiry(),
                cookieEntity.isSecure(),
                cookieEntity.isHttpOnly(),
                cookieEntity.getSameSite());
    }
}