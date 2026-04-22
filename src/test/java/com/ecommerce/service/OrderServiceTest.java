package com.ecommerce.service;

import com.ecommerce.entity.*;
import com.ecommerce.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OrderServiceTest {

    @InjectMocks
    private OrderService orderService;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private CartRepository cartRepository;
    @Mock
    private ProductRepository productRepository;


    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateOrder() {

        User user = User.builder()
                .id(1L)
                .email("test@mail.com")
                .build();

        Product product = Product.builder()
                .id(1L)
                .name("Phone")
                .price(BigDecimal.valueOf(100.0))

                .build();

        CartItem cartItem = CartItem.builder()
                .product(product)
                .quantity(1)
                .price(100.0)
                .totalPrice(100.0)
                .build();

        // ✅ FIX HERE
        Cart cart = Cart.builder()
                .user(user)
                .items(new ArrayList<>(List.of(cartItem))) // 🔥 IMPORTANT FIX
                .totalPrice(100.0)
                .build();

        when(cartRepository.findByUser(user)).thenReturn(Optional.of(cart));
        when(orderRepository.save(any(Order.class))).thenAnswer(i -> i.getArguments()[0]);

        Order order = orderService.createOrder(user);

        assertNotNull(order);
        assertEquals(1, order.getItems().size());
        assertEquals(100.0, order.getTotalAmount());

        verify(cartRepository).save(any(Cart.class)); // cart cleared
    }


    @Test
    void testUpdateOrderStatus() {

        Order order = new Order();
        order.setId(1L);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any())).thenReturn(order);

        Order result = orderService.updateOrderStatus(1L, OrderStatus.PAID);

        assertEquals(OrderStatus.PAID, result.getStatus());
    }
}
