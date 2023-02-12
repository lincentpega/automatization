package com.lincentpega.javawildberriesselfbuy.domain;

import lombok.Data;

@Data
public class SessionParams {
    String proxy = "default";
    String userAgent = "default";
    String resolution = "default";
}