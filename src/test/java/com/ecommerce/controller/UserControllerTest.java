package com.ecommerce.controller;

import com.ecommerce.config.JwtFilter;
import com.ecommerce.config.TestSecurityConfig;
import com.ecommerce.entity.User;
import com.ecommerce.service.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
@AutoConfigureMockMvc(addFilters = false)
@Import(TestSecurityConfig.class)
@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;
    @MockBean
    private JwtFilter jwtFilter;
    @Test
    void testGetAllUsers() throws Exception {

        User user = new User();
        user.setId(1L);

        Mockito.when(userService.getAllUsers())
                .thenReturn(List.of(user));

        mockMvc.perform(get("/api/users/all"))
                .andExpect(status().isOk());
    }
}
