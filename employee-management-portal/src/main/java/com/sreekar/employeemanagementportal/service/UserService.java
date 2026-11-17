
package com.sreekar.employeemanagementportal.service;

import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.sreekar.employeemanagementportal.dto.RegisterRequest;
import com.sreekar.employeemanagementportal.dto.LoginRequest;
import com.sreekar.employeemanagementportal.dto.AuthResponse;
import com.sreekar.employeemanagementportal.entity.User;
import com.sreekar.employeemanagementportal.repository.UserRepository;
import com.sreekar.employeemanagementportal.security.JwtService;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public UserService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public User registerUser(RegisterRequest request) {

        String email = request.getEmail().trim().toLowerCase();
        if (userRepository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("Email already registered");
        }

        User user = new User();
        user.setName(request.getName().trim());
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole("USER");

        return userRepository.save(user);
    }

    public AuthResponse loginUser(LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail().trim().toLowerCase())
                .orElseThrow(() ->
                        new IllegalArgumentException("Invalid email or password"));

        boolean passwordMatches = user.getPassword() != null
                && passwordEncoder.matches(
                        request.getPassword(),
                        user.getPassword()
                );

        // Actual password verification
        if (!passwordMatches) {
            throw new IllegalArgumentException("Invalid email or password");
        }

        String token = jwtService.generateToken(user.getEmail());

        return new AuthResponse(
                token,
                user.getName(),
                user.getEmail(),
                user.getRole()
        );
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }
}
