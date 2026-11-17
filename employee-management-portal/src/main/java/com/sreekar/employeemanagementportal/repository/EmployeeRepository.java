package com.sreekar.employeemanagementportal.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sreekar.employeemanagementportal.entity.Employee;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    Optional<Employee> findByUserEmail(String email);
}