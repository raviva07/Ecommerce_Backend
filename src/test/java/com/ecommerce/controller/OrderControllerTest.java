package com.ecommerce.controller;

import com.ecommerce.config.JwtFilter;
import com.ecommerce.config.TestSecurityConfig;
import com.ecommerce.entity.Order;
import com.ecommerce.entity.User;
import com.ecommerce.service.OrderService;
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
@WebMvcTest(OrderController.class)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderService orderService;

    @MockBean
    private JwtFilter jwtFilter;

    @MockBean
    private UserService userService;
    @Test
    void testGetAllOrders() throws Exception {

        User user = new User();
        user.setId(1L); // ✅ IMPORTANT

        Order order = new Order();
        order.setId(1L);
        order.setTotalAmount(100.0);
        order.setUser(user); // ✅ FIX

        Mockito.when(orderService.getAllOrders())
                .thenReturn(List.of(order));

        mockMvc.perform(get("/api/orders/all")
                .contentType("application/json"))
                .andExpect(status().isOk());
    }

}
