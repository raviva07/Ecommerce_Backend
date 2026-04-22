package com.ecommerce.service;

import com.ecommerce.entity.User;
import com.ecommerce.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetUserByEmail() {

        User user = User.builder()
                .email("test@mail.com")
                .build();

        when(userRepository.findByEmail("test@mail.com"))
                .thenReturn(Optional.of(user));

        User result = userService.getUserByEmail("test@mail.com");

        assertNotNull(result);
        assertEquals("test@mail.com", result.getEmail());
    }

    // 🔥 IMPORTANT NEGATIVE CASE
    @Test
    void testGetUserByEmail_NotFound() {

        when(userRepository.findByEmail("wrong@mail.com"))
                .thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> userService.getUserByEmail("wrong@mail.com"));
    }
}

