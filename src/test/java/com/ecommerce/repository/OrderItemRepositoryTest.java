package com.ecommerce.repository;

import com.ecommerce.entity.Order;
import com.ecommerce.entity.OrderItem;
import com.ecommerce.entity.OrderStatus;
import com.ecommerce.entity.Product;
import com.ecommerce.entity.Role;
import com.ecommerce.entity.User;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.math.BigDecimal;

@DataJpaTest
class OrderItemRepositoryTest {

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Test
    void testSaveOrderItem() {

        User user = userRepository.save(
                new User(null, "test", "user@mail.com", "pass", Role.CUSTOMER)
        );

        Order order = orderRepository.save(
                Order.builder()
                        .user(user)
                        .totalAmount(1000.0)
                        .status(OrderStatus.CREATED)
                        .build()
        );

        Product product = productRepository.save(
                Product.builder()
                        .name("Mouse")
                        .price(BigDecimal.valueOf(500))
                        .category("Electronics")
                        .stock(10)
                        .description("Mouse desc")
                        .isActive(true)
                        .build()
        );

        OrderItem item = OrderItem.builder()
                .order(order)
                .product(product)
                .quantity(2) // ✅ REQUIRED
                .price(500.0)
                .build();

        OrderItem saved = orderItemRepository.save(item);

        assertNotNull(saved.getId());
    }
}
