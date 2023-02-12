package com.lincentpega.javawildberriesselfbuy.infrastructure.adapter.out.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
public class SessionParamsEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String proxy;
    private String userAgent;
    private String resolution;
}
