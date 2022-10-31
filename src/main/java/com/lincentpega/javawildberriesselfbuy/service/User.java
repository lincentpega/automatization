package com.lincentpega.javawildberriesselfbuy.service;

import com.lincentpega.javawildberriesselfbuy.repository.CookieWrapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.openqa.selenium.Cookie;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.util.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@RedisHash("User")
public class User implements Serializable {

    @Id
    private String number;
    private HashSet<CookieWrapper> cookies;

    public User(String number, Set<Cookie> cookies) {
        this.number = number;

        HashSet<CookieWrapper> cookieWrappers = new HashSet<>();
        for (Cookie cookie : cookies) {
            cookieWrappers.add(new CookieWrapper(cookie));
        }
        this.cookies = cookieWrappers;
    }
}
