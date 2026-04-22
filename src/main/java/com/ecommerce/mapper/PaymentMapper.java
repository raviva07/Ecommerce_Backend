package com.ecommerce.mapper;

import com.ecommerce.dto.PaymentResponse;
import com.ecommerce.dto.PaymentVerifyResponse;
import com.ecommerce.entity.Payment;

import java.math.BigDecimal;

public class PaymentMapper {

    public static PaymentResponse toResponse(Payment payment) {
        if (payment == null) return null;

        // ✅ Safely extract orderId from the Payment’s Order
        Long orderId = payment.getOrder() != null ? payment.getOrder().getId() : null;

        return PaymentResponse.builder()
                .paymentId(payment.getId())
                .orderId(orderId) // <-- now valid
                .razorpayOrderId(payment.getRazorpayOrderId())
                .razorpayPaymentId(payment.getRazorpayPaymentId())
                .amount(payment.getAmount() != null ? BigDecimal.valueOf(payment.getAmount()) : BigDecimal.ZERO)
                .status(payment.getStatus())
                .message("Payment details fetched")
                .build();
    }

    public static PaymentVerifyResponse toVerifyResponse(boolean success) {
        return new PaymentVerifyResponse(
                success,
                success ? "Payment successful" : "Payment failed"
        );
    }
}

