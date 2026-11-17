package com.sreekar.employeemanagementportal;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import com.sreekar.employeemanagementportal.entity.User;
import com.sreekar.employeemanagementportal.repository.UserRepository;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SecurityIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private final com.fasterxml.jackson.databind.ObjectMapper objectMapper =
            new com.fasterxml.jackson.databind.ObjectMapper();

    @Test
    void unauthenticatedAdminRequestReturns401() throws Exception {
        mockMvc.perform(get("/api/admin/test"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void userRoleCannotAccessAdminApi() throws Exception {
        mockMvc.perform(get("/api/admin/test")
                        .with(user("employee@example.com").roles("USER")))
                .andExpect(status().isForbidden());
    }

    @Test
    void adminRoleCanAccessAdminApi() throws Exception {
        mockMvc.perform(get("/api/admin/test")
                        .with(user("admin@example.com").roles("ADMIN")))
                .andExpect(status().isOk());
    }

    @Test
    void localPreflightAllowsCredentialedFrontendOrigin() throws Exception {
        mockMvc.perform(options("/api/admin/employees")
                        .header(HttpHeaders.ORIGIN, "http://localhost:5174")
                        .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "POST")
                        .header(HttpHeaders.ACCESS_CONTROL_REQUEST_HEADERS, "content-type,x-xsrf-token"))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "http://localhost:5174"))
                .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS, "true"));
    }

    @Test
    void registrationHashesPasswordAndLoginCreatesUsableHttpOnlyCookie() throws Exception {
        String password = "StrongPass123!";
        mockMvc.perform(post("/api/auth/register")
                        .contentType("application/json")
                        .content("{\"name\":\"Test Employee\",\"email\":\"test.employee@example.com\",\"password\":\"" + password + "\"}"))
                .andExpect(status().isCreated());

        User registered = userRepository.findByEmail("test.employee@example.com").orElseThrow();
        org.junit.jupiter.api.Assertions.assertTrue(passwordEncoder.matches(password, registered.getPassword()));

        var loginResult = mockMvc.perform(post("/api/auth/login")
                        .contentType("application/json")
                        .content("{\"email\":\"test.employee@example.com\",\"password\":\"" + password + "\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.role").value("USER"))
                .andExpect(cookie().exists("ACCESS_TOKEN"))
                .andReturn();

        var accessToken = loginResult.getResponse().getCookie("ACCESS_TOKEN");
        mockMvc.perform(get("/api/test/profile").cookie(accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("test.employee@example.com"));
    }

    @Test
    void registrationAlwaysAssignsUserRoleAndRejectsDuplicateEmail() throws Exception {
        String payload = "{\"name\":\"Role Check\",\"email\":\"role.check@example.com\",\"password\":\"StrongPass123!\",\"role\":\"ADMIN\"}";
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isCreated())
                .andExpect(cookie().doesNotExist("ACCESS_TOKEN"));

        User created = userRepository.findByEmail("role.check@example.com").orElseThrow();
        org.junit.jupiter.api.Assertions.assertEquals("USER", created.getRole());

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isConflict());
    }

    @Test
    void registrationRejectsInvalidEmailAndWeakPassword() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Invalid\",\"email\":\"not-an-email\",\"password\":\"weakpass\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void adminCanCreateReadUpdateAndDeleteEmployeeRecords() throws Exception {
        String employeeJson = "{\"employeeCode\":\"EMP-TEST-1\",\"department\":\"Engineering\",\"designation\":\"Developer\",\"phone\":\"5550100\",\"joiningDate\":\"2026-01-15\"}";
        var created = mockMvc.perform(post("/api/admin/employees")
                        .with(user("admin@example.com").roles("ADMIN"))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(employeeJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.employeeCode").value("EMP-TEST-1"))
                .andReturn();
        long employeeId = objectMapper.readTree(created.getResponse().getContentAsString()).get("id").asLong();

        mockMvc.perform(get("/api/admin/employees/{id}", employeeId)
                        .with(user("admin@example.com").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.department").value("Engineering"));

        mockMvc.perform(put("/api/admin/employees/{id}", employeeId)
                        .with(user("admin@example.com").roles("ADMIN"))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(employeeJson.replace("Engineering", "Product")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.department").value("Product"));

        mockMvc.perform(get("/api/admin/employees")
                        .with(user("admin@example.com").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].employeeCode").value("EMP-TEST-1"));

        mockMvc.perform(delete("/api/admin/employees/{id}", employeeId)
                        .with(user("admin@example.com").roles("ADMIN"))
                        .with(csrf()))
                .andExpect(status().isOk());
        mockMvc.perform(get("/api/admin/employees/{id}", employeeId)
                        .with(user("admin@example.com").roles("ADMIN")))
                .andExpect(status().isNotFound());
    }
}
