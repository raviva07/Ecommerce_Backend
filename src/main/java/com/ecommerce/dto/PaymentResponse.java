package com.ecommerce.dto;

import com.ecommerce.entity.PaymentStatus;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentResponse {

    private Long paymentId;
    private Long orderId; 
    private String razorpayOrderId;
    private String razorpayPaymentId;
    private java.math.BigDecimal amount;
    private PaymentStatus status;
    private String message;
}
