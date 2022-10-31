package com.lincentpega.javawildberriesselfbuy.repository;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.openqa.selenium.Cookie;

@Data
@NoArgsConstructor
public class CookieWrapper {
    private String name;
    private String value;

    public CookieWrapper(Cookie cookie) {
        this.name = cookie.getName();
        this.value = cookie.getValue();
    }

}
