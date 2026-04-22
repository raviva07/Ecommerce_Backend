package com.ecommerce.controller;

import com.ecommerce.config.JwtFilter;
import com.ecommerce.config.TestSecurityConfig;
import com.ecommerce.dto.PaymentRequest;
import com.ecommerce.entity.Payment;
import com.ecommerce.service.PaymentService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.*;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
@AutoConfigureMockMvc(addFilters = false)
@Import(TestSecurityConfig.class)
@WebMvcTest(PaymentController.class)
class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PaymentService paymentService;
    @MockBean
    private JwtFilter jwtFilter;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testCreatePayment() throws Exception {

        PaymentRequest request = new PaymentRequest();
        request.setOrderId(1L);

        Payment payment = new Payment();
        payment.setAmount(1000.0);

        Mockito.when(paymentService.createPayment(1L))
                .thenReturn(payment);

        mockMvc.perform(post("/api/payment/create-order")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }
}

