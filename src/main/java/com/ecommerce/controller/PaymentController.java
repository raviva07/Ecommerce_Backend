package com.ecommerce.controller;

import com.ecommerce.config.CustomUserDetails;
import com.ecommerce.constants.ApiMessages;
import com.ecommerce.dto.PaymentRequest;
import com.ecommerce.dto.PaymentResponse;
import com.ecommerce.dto.PaymentVerifyRequest;
import com.ecommerce.dto.PaymentVerifyResponse;
import com.ecommerce.entity.Payment;
import com.ecommerce.mapper.PaymentMapper;
import com.ecommerce.response.ApiResponse;
import com.ecommerce.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    // CREATE PAYMENT ORDER
    @PostMapping("/create-order")
    public ApiResponse<PaymentResponse> createPayment(@Valid @RequestBody PaymentRequest request) {
        if (request.getOrderId() == null) {
            throw new IllegalArgumentException("Order ID is required");
        }
        return ApiResponse.<PaymentResponse>builder()
                .success(true)
                .message(ApiMessages.PAYMENT_CREATED)
                .data(PaymentMapper.toResponse(paymentService.createPayment(request.getOrderId())))
                .build();
    }

    // VERIFY PAYMENT
    @PostMapping("/verify")
    public ApiResponse<PaymentVerifyResponse> verifyPayment(@Valid @RequestBody PaymentVerifyRequest request) {
        boolean success = paymentService.verifyPayment(
                request.getRazorpayOrderId(),
                request.getPaymentId(),
                request.getSignature()
        );
        return ApiResponse.<PaymentVerifyResponse>builder()
                .success(success)
                .message(success ? ApiMessages.PAYMENT_SUCCESS : ApiMessages.PAYMENT_FAILED)
                .data(PaymentMapper.toVerifyResponse(success))
                .build();
    }

    // GET PAYMENT BY ORDER (customer only)
    @GetMapping("/{orderId}")
    public ApiResponse<PaymentResponse> getPaymentByOrder(@PathVariable Long orderId,
                                                          @AuthenticationPrincipal CustomUserDetails user) {
        Payment payment = paymentService.getPaymentByOrderIdForUser(orderId, user.getId());
        return ApiResponse.<PaymentResponse>builder()
                .success(true)
                .message("Payment fetched successfully")
                .data(PaymentMapper.toResponse(payment))
                .build();
    }

    // ✅ ADMIN: Get payment by order (bypasses user check)
    @GetMapping("/admin/{orderId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<PaymentResponse> getPaymentByOrderAdmin(@PathVariable Long orderId) {
        Payment payment = paymentService.getPaymentByOrderId(orderId);
        return ApiResponse.<PaymentResponse>builder()
                .success(true)
                .message("Admin payment fetched successfully")
                .data(PaymentMapper.toResponse(payment))
                .build();
    }

    // USER: Get my payment history
    @GetMapping("/history/me")
    public ApiResponse<List<PaymentResponse>> getMyPaymentHistory(@AuthenticationPrincipal CustomUserDetails user) {
        List<Payment> payments = paymentService.getPaymentsByUser(user.getId());
        return ApiResponse.<List<PaymentResponse>>builder()
                .success(true)
                .message("User payment history fetched successfully")
                .data(payments.stream().map(PaymentMapper::toResponse).toList())
                .build();
    }

    // ADMIN: Get all payments
    @GetMapping("/history")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<List<PaymentResponse>> getAllPayments() {
        List<Payment> payments = paymentService.getAllPayments();
        return ApiResponse.<List<PaymentResponse>>builder()
                .success(true)
                .message("All payments fetched successfully")
                .data(payments.stream().map(PaymentMapper::toResponse).toList())
                .build();
    }
}
