package com.ecommerce.repository;

import com.ecommerce.entity.Role;
import com.ecommerce.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void testFindByEmail() {

        User user = new User();
        user.setName("Test");
        user.setEmail("test@mail.com");
        user.setPassword("123456"); // ✅ REQUIRED
        user.setRole(Role.CUSTOMER);

        userRepository.save(user);

        Optional<User> result = userRepository.findByEmail("test@mail.com");

        assertTrue(result.isPresent());
        assertEquals("test@mail.com", result.get().getEmail());
    }

    @Test
    void testExistsByEmail() {

        User user = new User();
        user.setName("Test");
        user.setEmail("exists@mail.com");
        user.setPassword("123456"); // ✅ REQUIRED
        user.setRole(Role.CUSTOMER);

        userRepository.save(user);

        boolean exists = userRepository.existsByEmail("exists@mail.com");

        assertTrue(exists);
    }
}
