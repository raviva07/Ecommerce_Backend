package com.ecommerce.repository;

import com.ecommerce.entity.Order;
import com.ecommerce.entity.User;
import com.ecommerce.entity.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    // 👤 User orders
    List<Order> findByUser(User user);

    // 📊 Orders by status
    List<Order> findByStatus(OrderStatus status);

    // 👑 Admin - all orders sorted
    List<Order> findAllByOrderByCreatedAtDesc();

    // 📊 Count for dashboard
    long countByStatus(OrderStatus status);
}

