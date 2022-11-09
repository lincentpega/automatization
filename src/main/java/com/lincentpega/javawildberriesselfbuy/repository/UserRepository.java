package com.lincentpega.javawildberriesselfbuy.repository;

import com.lincentpega.javawildberriesselfbuy.model.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends CrudRepository<User, String> { }