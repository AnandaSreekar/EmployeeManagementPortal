
package com.sreekar.employeemanagementportal.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import java.time.Duration;
import java.util.Map;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.*;

import com.sreekar.employeemanagementportal.dto.RegisterRequest;
import com.sreekar.employeemanagementportal.dto.LoginRequest;
import com.sreekar.employeemanagementportal.dto.AuthResponse;
import com.sreekar.employeemanagementportal.service.UserService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Value("${jwt.expiration}")
    private long jwtExpiration;
    @Value("${app.cookie.secure:false}")
    private boolean cookieSecure;
    @Value("${app.cookie.same-site:Lax}")
    private String cookieSameSite;

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(
            @Valid @RequestBody RegisterRequest request) {

        try {
            userService.registerUser(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body("Registration successful");

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @Valid @RequestBody LoginRequest request) {

        try {
            AuthResponse response = userService.loginUser(request);
            ResponseCookie cookie = ResponseCookie.from("ACCESS_TOKEN", response.getToken())
                    .httpOnly(true).secure(cookieSecure).sameSite(cookieSameSite)
                    .path("/").maxAge(Duration.ofMillis(jwtExpiration)).build();
            return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString())
                    .body(response);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(e.getMessage());
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        ResponseCookie cookie = ResponseCookie.from("ACCESS_TOKEN", "")
                .httpOnly(true).secure(cookieSecure).sameSite(cookieSameSite)
                .path("/").maxAge(Duration.ZERO).build();
        return ResponseEntity.noContent().header(HttpHeaders.SET_COOKIE, cookie.toString()).build();
    }

    @GetMapping("/csrf")
    public Map<String, String> csrfToken(CsrfToken csrfToken) {
        return Map.of("token", csrfToken.getToken());
    }
}
