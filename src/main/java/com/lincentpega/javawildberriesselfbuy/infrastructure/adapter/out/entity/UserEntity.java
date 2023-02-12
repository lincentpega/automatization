package com.lincentpega.javawildberriesselfbuy.infrastructure.adapter.out.entity;

import com.lincentpega.javawildberriesselfbuy.domain.SessionParams;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@Setter
public class UserEntity {
    @Id
    private Long id;
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    private SessionParamsEntity sessionParams;
    @OneToMany
    private List<SessionCookiesEntity> sessionCookies;
}