
package com.sreekar.employeemanagementportal.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sreekar.employeemanagementportal.dto.EmployeeResponse;
import com.sreekar.employeemanagementportal.entity.Employee;
import com.sreekar.employeemanagementportal.entity.User;
import com.sreekar.employeemanagementportal.service.EmployeeService;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final EmployeeService employeeService;

    public UserController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getMyProfile(Authentication authentication) {

        try {
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

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        }
    }
}
