package com.ecommerce.repository;

import com.ecommerce.entity.Cart;
import com.ecommerce.entity.Role;
import com.ecommerce.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class CartRepositoryTest {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void testFindByUser() {

        User user = new User();
        user.setName("Test");
        user.setEmail("cart@mail.com");
        user.setPassword("123"); // ✅ REQUIRED
        user.setRole(Role.CUSTOMER);

        user = userRepository.save(user);

        Cart cart = new Cart();
        cart.setUser(user);
        cart.setTotalPrice(0.0);

        cartRepository.save(cart);

        Optional<Cart> result = cartRepository.findByUser(user);

        assertTrue(result.isPresent());
    }
}
