package com.sreekar.employeemanagementportal.dto;

import java.time.LocalDate;

public class EmployeeResponse {

    private Long id;
    private String employeeCode;
    private String name;
    private String email;
    private String department;
    private String designation;
    private String phone;
    private LocalDate joiningDate;

    public EmployeeResponse(
            Long id,
            String employeeCode,
            String name,
            String email,
            String department,
            String designation,
            String phone,
            LocalDate joiningDate) {

        this.id = id;
        this.employeeCode = employeeCode;
        this.name = name;
        this.email = email;
        this.department = department;
        this.designation = designation;
        this.phone = phone;
        this.joiningDate = joiningDate;
    }

    public Long getId() {
        return id;
    }

    public String getEmployeeCode() {
        return employeeCode;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getDepartment() {
        return department;
    }

    public String getDesignation() {
        return designation;
    }

    public String getPhone() {
        return phone;
    }

    public LocalDate getJoiningDate() {
        return joiningDate;
    }
}