package com.lincentpega.javawildberriesselfbuy.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class User {
    Long id;
    List<SessionInfo> sessionInfoList;
    SessionParams sessionParams;

    public User(Long id) {
        this.id = id;
        this.sessionInfoList = new ArrayList<>();
        this.sessionParams = new SessionParams();
    }
}