package com.lincentpega.javawildberriesselfbuy.infrastructure;

import com.lincentpega.javawildberriesselfbuy.domain.SessionCookie;
import org.mapstruct.Mapper;
import org.openqa.selenium.Cookie;

@Mapper(componentModel = "spring")
public interface CookieToSessionCookieMapper {
    SessionCookie cookieToSessionCookie(Cookie cookie);

    Cookie sessionCookieToCookie(SessionCookie sessionCookie);
}