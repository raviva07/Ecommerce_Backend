package com.ecommerce.service;

import com.ecommerce.entity.Order;
import com.ecommerce.entity.OrderStatus;
import com.ecommerce.entity.Payment;
import com.ecommerce.entity.PaymentStatus;
import com.ecommerce.repository.OrderRepository;
import com.ecommerce.repository.PaymentRepository;
import com.razorpay.RazorpayClient;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;

    @Value("${razorpay.key_id}")
    private String keyId;

    @Value("${razorpay.key_secret}")
    private String keySecret;

    // CREATE PAYMENT ORDER
    @Transactional
    public Payment createPayment(Long orderId) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        if (order.getStatus() == OrderStatus.PAID) {
            throw new IllegalStateException("Order already paid");
        }

        String razorpayOrderId;

        try {
            // ✅ If keys exist → real Razorpay
            if (keyId != null && keySecret != null) {

                RazorpayClient client = new RazorpayClient(keyId, keySecret);

                int amountPaise = (int) (order.getTotalAmount() * 100);

                JSONObject options = new JSONObject();
                options.put("amount", amountPaise);
                options.put("currency", "INR");
                options.put("receipt", "order_" + orderId);

                com.razorpay.Order razorpayOrder = client.orders.create(options);
                razorpayOrderId = razorpayOrder.get("id");

            } else {
                // ✅ TEST MODE (no Razorpay)
                razorpayOrderId = "test_order_" + System.currentTimeMillis();
            }

        } catch (Exception e) {
            throw new RuntimeException("Error creating Razorpay order: " + e.getMessage());
        }

        Payment payment = Payment.builder()
                .order(order)
                .razorpayOrderId(razorpayOrderId)
                .amount(order.getTotalAmount())
                .status(PaymentStatus.CREATED)
                .build();

        return paymentRepository.save(payment);
    }


 // VERIFY PAYMENT
    @Transactional
    public boolean verifyPayment(String razorpayOrderId, String paymentId, String signature) {
        try {
            Payment payment = paymentRepository.findByRazorpayOrderId(razorpayOrderId)
                    .orElseThrow(() -> new IllegalArgumentException("Payment not found"));

            if (payment.getStatus() == PaymentStatus.SUCCESS) {
                throw new IllegalStateException("Already verified");
            }

            boolean verified;

            // ✅ REAL MODE (when Razorpay key exists)
            if (keySecret != null && signature != null) {
                String generatedSignature = generateSignature(razorpayOrderId, paymentId);
                verified = generatedSignature.equals(signature);
            } else {
                // ✅ TEST MODE (skip verification)
                verified = true;
            }

            // ❌ If verification fails
            if (!verified) {
                payment.setStatus(PaymentStatus.FAILED);
                paymentRepository.save(payment);
                return false;
            }

            // ✅ SUCCESS FLOW
            payment.setRazorpayPaymentId(paymentId);
            payment.setRazorpaySignature(signature);
            payment.setStatus(PaymentStatus.SUCCESS);

            Order order = payment.getOrder();
            order.setStatus(OrderStatus.PAID);

            orderRepository.save(order);
            paymentRepository.save(payment);

            return true;

        } catch (Exception e) {
            throw new RuntimeException("Verification failed: " + e.getMessage());
        }
    }

    // SIGNATURE GENERATION
    private String generateSignature(String orderId, String paymentId) throws Exception {
        String data = orderId + "|" + paymentId;

        javax.crypto.Mac mac = javax.crypto.Mac.getInstance("HmacSHA256");
        javax.crypto.spec.SecretKeySpec secretKey =
                new javax.crypto.spec.SecretKeySpec(keySecret.getBytes(), "HmacSHA256");

        mac.init(secretKey);
        byte[] hash = mac.doFinal(data.getBytes());

        return bytesToHex(hash);
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder hex = new StringBuilder();
        for (byte b : bytes) {
            hex.append(String.format("%02x", b));
        }
        return hex.toString();
    }

    // CUSTOMER: Get payment by order (checks user ownership)
    public Payment getPaymentByOrderIdForUser(Long orderId, Long userId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (!order.getUser().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized access to payment");
        }

        return paymentRepository.findByOrder(order)
                .orElseThrow(() -> new RuntimeException("Payment not found"));
    }

    // ADMIN: Get payment by order (no user check)
    public Payment getPaymentByOrderId(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        return paymentRepository.findByOrder(order)
                .orElseThrow(() -> new RuntimeException("Payment not found"));
    }

    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }

    public List<Payment> getPaymentsByUser(Long userId) {
        return paymentRepository.findAllByOrder_User_Id(userId);
    }
}

