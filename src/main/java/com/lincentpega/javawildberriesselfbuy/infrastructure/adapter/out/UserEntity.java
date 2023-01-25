package com.lincentpega.javawildberriesselfbuy.infrastructure.adapter.out;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Set;

@Entity
public class UserEntity {
    @Id
    private Long userId;
    @ElementCollection
    private Set<String> numbers;
}