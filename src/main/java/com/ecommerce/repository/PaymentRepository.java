package com.ecommerce.repository;

import com.ecommerce.entity.Payment;
import com.ecommerce.entity.Order;
import com.ecommerce.entity.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    // 🔍 Find by order
    Optional<Payment> findByOrder(Order order);

    // 🔍 Find by Razorpay order ID
    Optional<Payment> findByRazorpayOrderId(String razorpayOrderId);

    // 🔍 Find by payment ID
    Optional<Payment> findByRazorpayPaymentId(String razorpayPaymentId);

    // 📊 Filter by status
    List<Payment> findByStatus(PaymentStatus status);

	List<Payment> findAllByOrder_User_Id(Long userId);
}

