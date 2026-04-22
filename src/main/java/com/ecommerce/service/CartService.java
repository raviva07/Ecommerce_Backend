package com.ecommerce.service;

import com.ecommerce.entity.*;
import com.ecommerce.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;

    // ================================
    // 🔹 Get or Create Cart
    // ================================
    public Cart getOrCreateCart(User user) {
        return cartRepository.findByUser(user)
                .orElseGet(() -> {
                    Cart newCart = Cart.builder()
                            .user(user)
                            .items(new ArrayList<>()) // ✅ mutable list
                            .totalPrice(0.0)
                            .build();
                    return cartRepository.save(newCart);
                });
    }

    // ================================
    // 🔹 Add to Cart
    // ================================
    public Cart addToCart(User user, Long productId, int quantity) {

        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than 0");
        }

        Cart cart = getOrCreateCart(user);

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        int stock = product.getStock() != null ? product.getStock() : Integer.MAX_VALUE;

        if (stock < quantity) {
            throw new IllegalArgumentException("Not enough stock available");
        }

        CartItem item = cartItemRepository.findByCartAndProduct(cart, product)
                .orElse(null);

        if (item != null) {

            int newQty = item.getQuantity() + quantity;

            if (stock < newQty) {
                throw new IllegalArgumentException("Stock limit exceeded");
            }

            item.setQuantity(newQty);

        } else {

            item = CartItem.builder()
                    .cart(cart)
                    .product(product)
                    .quantity(quantity)
                    .price(product.getPrice() != null ? product.getPrice().doubleValue() : 0.0)
                    .build();

            if (cart.getItems() == null) {
                cart.setItems(new ArrayList<>());
            }

            cart.getItems().add(item); // ✅ DO NOT replace list
        }

        cartItemRepository.save(item);

        updateTotal(cart);

        return cartRepository.save(cart);
    }

    // ================================
    // 🔹 Update Quantity
    // ================================
    public Cart updateQuantity(User user, Long productId, int quantity) {

        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than 0");
        }

        Cart cart = getOrCreateCart(user);

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        CartItem item = cartItemRepository.findByCartAndProduct(cart, product)
                .orElseThrow(() -> new IllegalArgumentException("Item not found"));

        int stock = product.getStock() != null ? product.getStock() : Integer.MAX_VALUE;

        if (stock < quantity) {
            throw new IllegalArgumentException("Not enough stock available");
        }

        item.setQuantity(quantity);

        cartItemRepository.save(item);

        updateTotal(cart);

        return cartRepository.save(cart);
    }

    // ================================
    // 🔹 Remove Item
    // ================================
    public Cart removeItem(User user, Long productId) {

        Cart cart = getOrCreateCart(user);

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        CartItem item = cartItemRepository.findByCartAndProduct(cart, product)
                .orElseThrow(() -> new IllegalArgumentException("Item not found"));

        // ✅ SAFE removal (Hibernate-friendly)
        if (cart.getItems() != null) {
            cart.getItems().removeIf(i -> i.getId().equals(item.getId()));
        }

        cartItemRepository.delete(item);

        updateTotal(cart);

        return cartRepository.save(cart);
    }

    // ================================
    // 🔹 Clear Cart (FIXED CORE ISSUE)
    // ================================
    public Cart clearCart(User user) {

        Cart cart = getOrCreateCart(user);

        if (cart.getItems() != null && !cart.getItems().isEmpty()) {

            // ✅ VERY IMPORTANT: DO NOT replace list
            List<CartItem> itemsCopy = new ArrayList<>(cart.getItems());

            for (CartItem item : itemsCopy) {
                cart.getItems().remove(item);   // remove from cart
                cartItemRepository.delete(item); // delete from DB
            }
        }

        cart.setTotalPrice(0.0);

        return cartRepository.save(cart);
    }

    // ================================
    // 🔹 Update Total
    // ================================
    private void updateTotal(Cart cart) {

        double total = 0.0;

        if (cart.getItems() != null) {
            total = cart.getItems().stream()
                    .mapToDouble(item -> item.getPrice() * item.getQuantity())
                    .sum();
        }

        cart.setTotalPrice(total);
    }
}
