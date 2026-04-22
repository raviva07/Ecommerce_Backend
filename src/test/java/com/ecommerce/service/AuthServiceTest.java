package com.ecommerce.service;

import com.ecommerce.dto.*;
import com.ecommerce.entity.*;
import com.ecommerce.repository.UserRepository;
import com.ecommerce.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthServiceTest {

    @InjectMocks
    private AuthService authService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRegister() {

        RegisterRequest request = new RegisterRequest();
        request.setEmail("test@mail.com");
        request.setPassword("123456");

        when(userRepository.existsByEmail("test@mail.com")).thenReturn(false);
        when(passwordEncoder.encode(any())).thenReturn("encoded");
        when(jwtUtil.generateToken(any())).thenReturn("token");

        AuthResponse response = authService.register(request);

        assertNotNull(response);
        assertEquals("token", response.getToken());
    }

    @Test
    void testLogin() {

        User user = new User();
        user.setEmail("test@mail.com");
        user.setPassword("encoded");

        when(userRepository.findByEmail("test@mail.com"))
                .thenReturn(Optional.of(user));
        when(passwordEncoder.matches("123456", "encoded"))
                .thenReturn(true);
        when(jwtUtil.generateToken(any())).thenReturn("token");

        AuthRequest request = new AuthRequest();
        request.setEmail("test@mail.com");
        request.setPassword("123456");

        AuthResponse response = authService.login(request);

        assertEquals("token", response.getToken());
    }
}

