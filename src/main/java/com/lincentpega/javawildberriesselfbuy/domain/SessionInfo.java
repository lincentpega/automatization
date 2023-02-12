package com.lincentpega.javawildberriesselfbuy.domain;

import lombok.Data;
import org.openqa.selenium.Cookie;

import java.util.Set;

@Data
public class SessionInfo {
    Long number;
    Set<Cookie> cookies;
}