package com.sreekar.employeemanagementportal.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import com.sreekar.employeemanagementportal.dto.EmployeeRequest;
import com.sreekar.employeemanagementportal.dto.EmployeeResponse;
import com.sreekar.employeemanagementportal.entity.Employee;
import com.sreekar.employeemanagementportal.entity.User;
import com.sreekar.employeemanagementportal.repository.UserRepository;
import com.sreekar.employeemanagementportal.service.EmployeeService;

@RestController
@RequestMapping("/api/admin/employees")
public class EmployeeController {

    private final EmployeeService employeeService;
    private final UserRepository userRepository;

    public EmployeeController(
            EmployeeService employeeService,
            UserRepository userRepository) {
        this.employeeService = employeeService;
        this.userRepository = userRepository;
    }

    @PostMapping
    public ResponseEntity<?> createEmployee(
            @Valid @RequestBody EmployeeRequest request) {

        try {
            Employee employee = new Employee();
            copyRequestData(request, employee);

            Employee saved = employeeService.saveEmployee(employee);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(toResponse(saved));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping
    public List<EmployeeResponse> getAllEmployees() {
        return employeeService.getAllEmployees()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmployeeResponse> getEmployeeById(
            @PathVariable Long id) {

        Employee employee = employeeService.getEmployeeById(id);
        return ResponseEntity.ok(toResponse(employee));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateEmployee(
            @PathVariable Long id,
            @Valid @RequestBody EmployeeRequest request) {

        try {
            Employee employee = employeeService.getEmployeeById(id);

            copyRequestData(request, employee);

            Employee updated = employeeService.saveEmployee(employee);

            return ResponseEntity.ok(toResponse(updated));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteEmployee(@PathVariable Long id) {

        employeeService.getEmployeeById(id);
        employeeService.deleteEmployee(id);

        return ResponseEntity.ok("Employee deleted successfully");
    }

    private void copyRequestData(
            EmployeeRequest request,
            Employee employee) {

        employee.setEmployeeCode(request.getEmployeeCode());
        employee.setDepartment(request.getDepartment());
        employee.setDesignation(request.getDesignation());
        employee.setPhone(request.getPhone());
        employee.setJoiningDate(request.getJoiningDate());

        if (request.getUserId() != null) {
            User user = userRepository.findById(request.getUserId())
                    .orElseThrow(() ->
                            new IllegalArgumentException("User not found"));

            employee.setUser(user);
        }
    }

    private EmployeeResponse toResponse(Employee employee) {

        User user = employee.getUser();

        return new EmployeeResponse(
                employee.getId(),
                employee.getEmployeeCode(),
                user != null ? user.getName() : null,
                user != null ? user.getEmail() : null,
                employee.getDepartment(),
                employee.getDesignation(),
                employee.getPhone(),
                employee.getJoiningDate()
        );
    }
}
