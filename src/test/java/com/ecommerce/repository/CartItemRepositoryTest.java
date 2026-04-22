package com.ecommerce.repository;

import com.ecommerce.entity.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class CartItemRepositoryTest {

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void testFindByCartAndProduct() {

        User user = userRepository.save(
                new User(null, "test", "test@mail.com", "pass", Role.CUSTOMER)
        );

        Cart cart = cartRepository.save(
                new Cart(null, user, null, 0.0)
        );

        Product product = productRepository.save(
                Product.builder()
                        .name("Mouse")
                        .price(BigDecimal.valueOf(500))
                        .category("Electronics") // ✅ REQUIRED
                        .stock(10)
                        .description("Mouse desc")
                        .isActive(true)
                        .build()
        );

        CartItem item = CartItem.builder()
                .cart(cart)
                .product(product)
                .quantity(2)
                .price(500.0)
                .build();

        cartItemRepository.save(item);

        Optional<CartItem> result =
                cartItemRepository.findByCartAndProduct(cart, product);

        assertTrue(result.isPresent());
    }
}
