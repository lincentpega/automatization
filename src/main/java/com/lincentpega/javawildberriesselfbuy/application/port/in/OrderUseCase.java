package com.lincentpega.javawildberriesselfbuy.application.port.in;

import org.springframework.core.io.ByteArrayResource;

public interface OrderUseCase {
    void enterAddress(String address);

    void purchase();
}