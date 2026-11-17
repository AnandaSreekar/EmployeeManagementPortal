
package com.sreekar.employeemanagementportal.controller;

import java.util.Map;
import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.sreekar.employeemanagementportal.repository.UserRepository;
import com.sreekar.employeemanagementportal.entity.User;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final UserRepository userRepository;

    public AdminController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/test")
    public Map<String, String> testAdminAccess(
            Authentication authentication) {

        return Map.of(
                "message", "ADMIN access successful",
                "email", authentication.getName(),
                "role", "ADMIN"
        );
    }

    @GetMapping("/users")
    public List<Map<String, Object>> getUsers() {
        return userRepository.findAll().stream().map(user -> Map.<String, Object>of(
                "id", user.getId(), "name", user.getName(),
                "email", user.getEmail(), "role", user.getRole()))
                .toList();
    }
}
