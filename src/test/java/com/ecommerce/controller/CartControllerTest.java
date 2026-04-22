package com.ecommerce.controller;

import com.ecommerce.config.JwtFilter;
import com.ecommerce.config.TestSecurityConfig;
import com.ecommerce.entity.Cart;
import com.ecommerce.entity.User;
import com.ecommerce.service.CartService;
import com.ecommerce.service.UserService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CartController.class)
@Import(TestSecurityConfig.class)   // ✅ IMPORTANT
class CartControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CartService cartService;

    @MockBean
    private JwtFilter jwtFilter;

    @MockBean
    private UserService userService;

    private static final String BASE_URL = "/api/cart";

    // ✅ SAFE CART OBJECT
    private Cart createCart() {
        Cart cart = new Cart();
        cart.setItems(new ArrayList<>());
        cart.setTotalPrice(0.0);
        return cart;
    }

    @Test
    void testGetCart() throws Exception {

        User user = new User();
        user.setEmail("test@mail.com");

        when(userService.getUserByEmail(anyString())).thenReturn(user);
        when(cartService.getOrCreateCart(any(User.class))).thenReturn(createCart());

        mockMvc.perform(get(BASE_URL)
                        .with(user("test@mail.com").roles("USER")))
                .andExpect(status().isOk());
    }

    @Test
    void testAddToCart() throws Exception {

        User user = new User();

        when(userService.getUserByEmail(anyString())).thenReturn(user);
        when(cartService.addToCart(any(User.class), anyLong(), anyInt()))
                .thenReturn(createCart());

        mockMvc.perform(post(BASE_URL + "/add")
                        .param("productId", "1")
                        .param("quantity", "2")
                        .with(user("test@mail.com").roles("USER")))
                .andExpect(status().isOk());
    }

    @Test
    void testUpdateCart() throws Exception {

        User user = new User();

        when(userService.getUserByEmail(anyString())).thenReturn(user);
        when(cartService.updateQuantity(any(User.class), anyLong(), anyInt()))
                .thenReturn(createCart());

        mockMvc.perform(put(BASE_URL + "/update")
                        .param("productId", "1")
                        .param("quantity", "5")
                        .with(user("test@mail.com").roles("USER")))
                .andExpect(status().isOk());
    }

    @Test
    void testRemoveItem() throws Exception {

        User user = new User();

        when(userService.getUserByEmail(anyString())).thenReturn(user);

        mockMvc.perform(delete(BASE_URL + "/remove")
                        .param("productId", "1")
                        .with(user("test@mail.com").roles("USER")))
                .andExpect(status().isOk());
    }

    @Test
    void testClearCart() throws Exception {

        User user = new User();

        when(userService.getUserByEmail(anyString())).thenReturn(user);

        mockMvc.perform(delete(BASE_URL + "/clear")
                        .with(user("test@mail.com").roles("USER")))
                .andExpect(status().isOk());
    }
}
