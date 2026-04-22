package com.ecommerce.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 📦 Order reference
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    // 💳 Razorpay Order ID
    @Column(nullable = false, unique = true)
    private String razorpayOrderId;

    // 💳 Razorpay Payment ID
    private String razorpayPaymentId;

    // 🔐 Signature verification
    private String razorpaySignature;

    // 💰 Amount paid
    @Column(nullable = false)
    private Double amount;

    // 📊 Status (CREATED, SUCCESS, FAILED)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status;

    // ⏱️ Timestamp
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
