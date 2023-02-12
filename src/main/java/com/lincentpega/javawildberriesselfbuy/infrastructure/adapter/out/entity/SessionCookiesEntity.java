package com.lincentpega.javawildberriesselfbuy.infrastructure.adapter.out.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.Set;

@Entity
@Getter
@Setter
public class SessionCookiesEntity {
    @Id
    private Long number;
    @OneToMany
    private Set<CookieEntity> cookie;
}
