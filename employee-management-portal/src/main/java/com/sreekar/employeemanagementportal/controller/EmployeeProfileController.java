package com.sreekar.employeemanagementportal.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.sreekar.employeemanagementportal.dto.EmployeeResponse;
import com.sreekar.employeemanagementportal.entity.Employee;
import com.sreekar.employeemanagementportal.entity.User;
import com.sreekar.employeemanagementportal.service.EmployeeService;

@RestController
@RequestMapping("/api/user")
public class EmployeeProfileController {

    private final EmployeeService employeeService;

    public EmployeeProfileController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping("/my-profile")
    public ResponseEntity<EmployeeResponse> getMyProfile(
            Authentication authentication) {

        String email = authentication.getName();

        Employee employee = employeeService.getEmployeeByEmail(email);

        User user = employee.getUser();

        EmployeeResponse response = new EmployeeResponse(
                employee.getId(),
                employee.getEmployeeCode(),
                user.getName(),
                user.getEmail(),
                employee.getDepartment(),
                employee.getDesignation(),
                employee.getPhone(),
                employee.getJoiningDate()
        );

        return ResponseEntity.ok(response);
    }
}