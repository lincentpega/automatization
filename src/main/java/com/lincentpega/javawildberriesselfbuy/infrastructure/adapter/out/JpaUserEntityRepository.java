package com.lincentpega.javawildberriesselfbuy.infrastructure.adapter.out;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaUserEntityRepository extends JpaRepository<UserEntity, Long> {
}