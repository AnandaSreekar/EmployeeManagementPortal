package com.sreekar.employeemanagementportal.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sreekar.employeemanagementportal.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

}