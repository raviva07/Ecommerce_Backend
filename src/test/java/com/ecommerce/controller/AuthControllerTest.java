package com.ecommerce.controller;

import com.ecommerce.config.JwtFilter;
import com.ecommerce.config.TestSecurityConfig;
import com.ecommerce.dto.*;
import com.ecommerce.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
@AutoConfigureMockMvc(addFilters = false)
@Import(TestSecurityConfig.class)
@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @MockBean
    private JwtFilter jwtFilter;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testRegister() throws Exception {

        RegisterRequest request = new RegisterRequest();
        request.setName("Test");
        request.setEmail("test@mail.com");
        request.setPassword("123456");

        AuthResponse response = new AuthResponse();
        response.setToken("token");

        Mockito.when(authService.register(Mockito.any()))
                .thenReturn(response);

        mockMvc.perform(post("/api/auth/register")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.token").value("token")); // ✅ FIXED
    }

    @Test
    void testLogin() throws Exception {

        AuthRequest request = new AuthRequest();
        request.setEmail("test@mail.com");
        request.setPassword("123456");

        AuthResponse response = new AuthResponse();
        response.setToken("token");

        Mockito.when(authService.login(Mockito.any()))
                .thenReturn(response);

        mockMvc.perform(post("/api/auth/login")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.token").value("token")); // ✅ FIXED
    }
}
