
package com.sreekar.employeemanagementportal.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.sreekar.employeemanagementportal.dto.EmployeeRequest;
import com.sreekar.employeemanagementportal.entity.Employee;
import com.sreekar.employeemanagementportal.entity.User;
import com.sreekar.employeemanagementportal.repository.EmployeeRepository;
import com.sreekar.employeemanagementportal.repository.UserRepository;

@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final UserRepository userRepository;

    public EmployeeService(
            EmployeeRepository employeeRepository,
            UserRepository userRepository) {
        this.employeeRepository = employeeRepository;
        this.userRepository = userRepository;
    }

    public Employee createEmployee(EmployeeRequest request) {

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() ->
                    new IllegalArgumentException("User not found"));

        Employee employee = new Employee();

        employee.setEmployeeCode(request.getEmployeeCode());
        employee.setDepartment(request.getDepartment());
        employee.setDesignation(request.getDesignation());
        employee.setPhone(request.getPhone());
        employee.setJoiningDate(request.getJoiningDate());
        employee.setUser(user);

        return employeeRepository.save(employee);
    }

    public Employee saveEmployee(Employee employee) {
        return employeeRepository.save(employee);
    }

    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    public Employee getEmployeeById(Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() ->
                    new IllegalArgumentException("Employee not found"));
    }

    public Employee getEmployeeByEmail(String email) {
        return employeeRepository.findByUserEmail(email)
                .orElseThrow(() ->
                    new IllegalArgumentException("Employee profile not found"));
    }

    public void deleteEmployee(Long id) {
        employeeRepository.deleteById(id);
    }
}
