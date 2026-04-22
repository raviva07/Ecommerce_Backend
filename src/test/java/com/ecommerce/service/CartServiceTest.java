package com.ecommerce.service;

import com.ecommerce.entity.*;
import com.ecommerce.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CartServiceTest {

    @InjectMocks
    private CartService cartService;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private ProductRepository productRepository;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAddToCart() {

        User user = new User();
        Cart cart = new Cart();
        cart.setUser(user);

        Product product = new Product();
        product.setId(1L);
        product.setPrice(BigDecimal.valueOf(100));

        when(cartRepository.findByUser(user)).thenReturn(Optional.of(cart));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(cartItemRepository.findByCartAndProduct(cart, product))
                .thenReturn(Optional.empty());

        when(cartRepository.save(any())).thenReturn(cart);

        Cart result = cartService.addToCart(user, 1L, 2);

        assertNotNull(result);
    }

    @Test
    void testClearCart() {

        User user = new User();
        Cart cart = new Cart();
        cart.setUser(user);

        when(cartRepository.findByUser(user)).thenReturn(Optional.of(cart));

        cartService.clearCart(user);

        verify(cartRepository).save(cart);
    }
}

