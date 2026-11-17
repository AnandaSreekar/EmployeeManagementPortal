package com.sreekar.employeemanagementportal.security;

import java.io.IOException;
import java.time.Duration;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.sreekar.employeemanagementportal.entity.User;
import com.sreekar.employeemanagementportal.repository.UserRepository;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final UserRepository userRepository;
    private final JwtService jwtService;

    @Value("${jwt.expiration}")
    private long jwtExpiration;
    @Value("${app.frontend-url:http://localhost:5173}")
    private String frontendUrl;
    @Value("${app.cookie.secure:false}")
    private boolean cookieSecure;
    @Value("${app.cookie.same-site:Lax}")
    private String cookieSameSite;

    public OAuth2SuccessHandler(
            UserRepository userRepository,
            JwtService jwtService) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
    }

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication)
            throws IOException, ServletException {

        OAuth2User googleUser = (OAuth2User) authentication.getPrincipal();
        
        String googleEmail = googleUser.getAttribute("email");
        String name = googleUser.getAttribute("name");
        Boolean emailVerified = googleUser.getAttribute("email_verified");

        if (googleEmail == null || !Boolean.TRUE.equals(emailVerified)) {
            response.sendRedirect(frontendUrl.replaceAll("/$", "") + "/login?error=google-email-unverified");
            return;
        }
        String email = googleEmail.trim().toLowerCase(Locale.ROOT);

        User user = userRepository.findByEmail(email)
                .orElseGet(() -> {
                    User newUser = new User();
                    newUser.setName(name == null || name.isBlank() ? email : name.trim());
                    newUser.setEmail(email);
                    newUser.setRole("USER");
                    return userRepository.save(newUser);
                });

        String token = jwtService.generateToken(user.getEmail());

        ResponseCookie cookie = ResponseCookie.from("ACCESS_TOKEN", token)
                .httpOnly(true)
                .secure(cookieSecure)
                .sameSite(cookieSameSite)
                .path("/")
                .maxAge(Duration.ofMillis(jwtExpiration))
                .build();

        response.addHeader(
                HttpHeaders.SET_COOKIE,
                cookie.toString()
        );

        response.sendRedirect(frontendUrl.replaceAll("/$", "") + "/oauth2/success");
    }
}
