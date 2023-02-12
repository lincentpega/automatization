package com.lincentpega.javawildberriesselfbuy.infrastructure.adapter.out.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Date;

@Entity
@Getter
@Setter
public class CookieEntity {
    @Id
    private Long id;
    private String name;
    private String value;
    private String path;
    private String domain;
    private Date expiry;
    private boolean isSecure;
    private boolean isHttpOnly;
    private String sameSite;
}