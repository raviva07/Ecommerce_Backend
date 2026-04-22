package com.ecommerce.repository;

import com.ecommerce.entity.CartItem;
import com.ecommerce.entity.Cart;
import com.ecommerce.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    // 🔍 Find all items in cart
    List<CartItem> findByCart(Cart cart);

    // 🔍 Check if product already exists in cart
    Optional<CartItem> findByCartAndProduct(Cart cart, Product product);

    // ❌ Remove all items from cart
    void deleteByCart(Cart cart);
}

