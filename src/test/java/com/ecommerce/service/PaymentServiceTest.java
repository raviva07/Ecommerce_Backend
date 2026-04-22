package com.ecommerce.service;

import com.ecommerce.entity.*;
import com.ecommerce.repository.OrderRepository;
import com.ecommerce.repository.PaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PaymentServiceTest {

    @InjectMocks
    private PaymentService paymentService;

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private OrderRepository orderRepository;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreatePayment() {

        Order order = new Order();
        order.setId(1L);
        order.setTotalAmount(500.0);

        when(orderRepository.findById(1L))
                .thenReturn(Optional.of(order));

        when(paymentRepository.save(any(Payment.class)))
                .thenAnswer(i -> i.getArgument(0));

        Payment result = paymentService.createPayment(1L);

        assertNotNull(result);
        assertEquals(500.0, result.getAmount());
        assertEquals(order, result.getOrder());
    }

    @Test
    void testVerifyPayment() {

        Payment payment = new Payment();
        payment.setRazorpayOrderId("order123");
        payment.setOrder(new Order());

        when(paymentRepository.findByRazorpayOrderId("order123"))
                .thenReturn(Optional.of(payment));

        when(paymentRepository.save(any())).thenReturn(payment);

        boolean result = paymentService.verifyPayment(
                "order123", "pay123", "sign123");

        assertTrue(result);
    }
}
 