package com.lincentpega.javawildberriesselfbuy.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
public class SessionCookie {
    private String number;
    private String name;
    private String value;
    private String path;
    private String domain;
    private Date expiry;
    private boolean isSecure;
    private boolean isHttpOnly;
    private String sameSite;
}