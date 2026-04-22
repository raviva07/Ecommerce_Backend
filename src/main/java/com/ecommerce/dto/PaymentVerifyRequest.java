package com.ecommerce.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentVerifyRequest {

    @NotBlank
    private String razorpayOrderId;

    @NotBlank
    private String paymentId;

    @NotBlank
    private String signature;
}
