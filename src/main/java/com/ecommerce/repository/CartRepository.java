package com.ecommerce.repository;

import com.ecommerce.entity.Cart;
import com.ecommerce.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {

    // 👤 Get cart by user
    Optional<Cart> findByUser(User user);

    // 🧹 Delete cart when needed
    void deleteByUser(User user);
}
