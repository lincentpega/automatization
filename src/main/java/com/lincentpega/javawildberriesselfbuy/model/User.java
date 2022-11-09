package com.lincentpega.javawildberriesselfbuy.model;

import com.lincentpega.javawildberriesselfbuy.dto.CookieDto;
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
    private HashSet<CookieDto> cookies;

    public User(String number, Set<Cookie> cookies) {
        this.number = number;

        HashSet<CookieDto> cookieDtos = new HashSet<>();
        for (Cookie cookie : cookies) {
            cookieDtos.add(new CookieDto(cookie));
        }
        this.cookies = cookieDtos;
    }
}
