package com.lincentpega.javawildberriesselfbuy.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.openqa.selenium.Cookie;

import java.util.Date;

@Data
@NoArgsConstructor
@ToString
public class CookieDto {
    private String name;
    private String value;
    private String path;
    private String domain;
    private Date expiry;
    private boolean isSecure;
    private boolean isHttpOnly;
    private String sameSite;

    public CookieDto(Cookie cookie) {
        this.name = cookie.getName();
        this.value = cookie.getValue();
        this.path = cookie.getPath();
        this.domain = cookie.getDomain();
        this.expiry = cookie.getExpiry();
        this.isSecure = cookie.isSecure();
        this.isHttpOnly = cookie.isHttpOnly();
        this.sameSite = cookie.getSameSite();
    }

}
