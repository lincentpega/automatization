package com.lincentpega.javawildberriesselfbuy.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
public class User {
    Long userId;
    Set<String> numbers;
}