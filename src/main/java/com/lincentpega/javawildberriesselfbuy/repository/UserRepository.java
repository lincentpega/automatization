package com.lincentpega.javawildberriesselfbuy.repository;

import com.lincentpega.javawildberriesselfbuy.dao.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends CrudRepository<User, String> { }