package com.ecommerce.repository;

import com.ecommerce.entity.Order;
import com.ecommerce.entity.OrderStatus;
import com.ecommerce.entity.Role;
import com.ecommerce.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class OrderRepositoryTest {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void testSaveOrder() {

        User user = userRepository.save(
                new User(null, "test", "user@mail.com", "pass", Role.CUSTOMER)
        );

        Order order = new Order();
        order.setUser(user);
        order.setTotalAmount(1000.0); // ✅ REQUIRED
        order.setStatus(OrderStatus.CREATED);

        Order saved = orderRepository.save(order);

        assertNotNull(saved.getId());
    }

    @Test
    void testFindByUser() {

        User user = userRepository.save(
                new User(null, "test", "user@mail.com", "pass", Role.CUSTOMER)
        );

        Order order = new Order();
        order.setUser(user);
        order.setTotalAmount(1000.0); // ✅ REQUIRED
        order.setStatus(OrderStatus.CREATED);

        orderRepository.save(order);

        List<Order> orders = orderRepository.findByUser(user);

        assertFalse(orders.isEmpty());
    }
}
