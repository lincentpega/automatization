package com.lincentpega.javawildberriesselfbuy.application.port.in;

import com.lincentpega.javawildberriesselfbuy.domain.Good;

import java.util.List;

public interface AddGoodUseCase {
    List<Good> addGood(String goodURL);
}