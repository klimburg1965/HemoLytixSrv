package de.hemolytix.server.controller;

import de.hemolytix.server.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AuthControllerTest extends BaseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @Test
    void login_WithValidCredentials_ShouldReturnOk() throws Exception {
        User dummyUser = new User("doctor_user", "hashedPassword", "Dr. Max", "DOCTOR");

        when(userRepository.findByUsername("doctor_user")).thenReturn(Optional.of(dummyUser));
        when(passwordEncoder.matches("correctPassword", "hashedPassword")).thenReturn(true);
        when(jwtUtils.generateToken("doctor_user", "DOCTOR")).thenReturn("mocked-jwt-token");

        String loginJson = """
                {
                    "username": "doctor_user",
                    "password": "correctPassword"
                }
                """;

        mockMvc.perform(post("/api/auth/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("mocked-jwt-token"))
                .andExpect(jsonPath("$.username").value("doctor_user"))
                .andExpect(jsonPath("$.fullName").value("Dr. Max"))
                .andExpect(jsonPath("$.role").value("DOCTOR"));
    }

    @Test
    void login_WithInvalidCredentials_ShouldReturnUnauthorized() throws Exception {
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());

        String invalidLoginJson = """
                {
                    "username": "doctor_user",
                    "password": "wrongPassword"
                }
                """;

        mockMvc.perform(post("/api/auth/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidLoginJson))
                .andExpect(status().isUnauthorized());
    }
}