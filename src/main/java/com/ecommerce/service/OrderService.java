package com.ecommerce.service;

import com.ecommerce.entity.*;
import com.ecommerce.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;

    // ================================
    // 🔹 CREATE ORDER
    // ================================
    @Transactional
    public Order createOrder(User user) {

        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        if (cart.getItems() == null || cart.getItems().isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }

        Order order = Order.builder()
                .user(user)
                .totalAmount(cart.getTotalPrice() != null ? cart.getTotalPrice() : 0.0)
                .status(OrderStatus.CREATED)
                .items(new ArrayList<>()) // ✅ mutable list
                .build();

        order = orderRepository.save(order);

        for (CartItem cartItem : cart.getItems()) {

            Product product = cartItem.getProduct();

            if (product == null) {
                throw new RuntimeException("Product missing");
            }

            if (product.getId() != null) {
                product = productRepository.findById(product.getId())
                        .orElse(product);
            }

            int stock = product.getStock() != null ? product.getStock() : Integer.MAX_VALUE;

            if (stock < cartItem.getQuantity()) {
                throw new RuntimeException("Out of stock");
            }

            // 🔹 Reduce stock
            product.setStock(stock - cartItem.getQuantity());
            productRepository.save(product);

            OrderItem item = OrderItem.builder()
                    .order(order)
                    .product(product)
                    .quantity(cartItem.getQuantity())
                    .price(cartItem.getPrice() != null ? cartItem.getPrice() : 0.0)
                    .build();

            orderItemRepository.save(item);
            order.getItems().add(item); // ✅ DO NOT replace list
        }

        order = orderRepository.save(order);

        // ================================
        // 🔥 FIXED: CLEAR CART SAFELY
        // ================================
        if (cart.getItems() != null && !cart.getItems().isEmpty()) {

            List<CartItem> itemsCopy = new ArrayList<>(cart.getItems());

            for (CartItem item : itemsCopy) {
                cart.getItems().remove(item);   // remove from list
                // optional: delete explicitly if not relying fully on orphanRemoval
                // cartItemRepository.delete(item);
            }
        }

        cart.setTotalPrice(0.0);

        cartRepository.save(cart);

        return order;
    }

    // ================================
    // 🔹 USER ORDERS
    // ================================
    public List<Order> getUserOrders(User user) {
        return orderRepository.findByUser(user);
    }

    // ================================
    // 🔹 ALL ORDERS
    // ================================
    public List<Order> getAllOrders() {
        return orderRepository.findAllByOrderByCreatedAtDesc();
    }

    // ================================
    // 🔹 GET ORDER BY ID
    // ================================
    public Order getOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));
    }

    // ================================
    // 🔹 UPDATE ORDER STATUS
    // ================================
    @Transactional
    public Order updateOrderStatus(Long id, OrderStatus status) {

        Order order = getOrderById(id);

        if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new RuntimeException("Already cancelled");
        }

        order.setStatus(status);

        return orderRepository.save(order);
    }

    // ================================
    // 🔹 CANCEL ORDER (USER)
    // ================================
    @Transactional
    public Order cancelOrder(Long id, User user) {

        Order order = getOrderById(id);

        if (order.getUser() != null &&
                !order.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized");
        }

        if (order.getStatus() == OrderStatus.PAID) {
            throw new RuntimeException("Cannot cancel paid order");
        }

        restoreStock(order);

        order.setStatus(OrderStatus.CANCELLED);

        return orderRepository.save(order);
    }

    // ================================
    // 🔹 CANCEL ORDER (ADMIN)
    // ================================
    @Transactional
    public Order adminCancelOrder(Long id) {

        Order order = getOrderById(id);

        if (order.getStatus() == OrderStatus.PAID) {
            throw new RuntimeException("Cannot cancel paid order");
        }

        restoreStock(order);

        order.setStatus(OrderStatus.CANCELLED);

        return orderRepository.save(order);
    }

    // ================================
    // 🔹 RESTORE STOCK
    // ================================
    private void restoreStock(Order order) {

        if (order.getItems() == null) return;

        for (OrderItem item : order.getItems()) {

            Product product = item.getProduct();
            if (product == null) continue;

            int stock = product.getStock() != null ? product.getStock() : 0;

            product.setStock(stock + item.getQuantity());
            productRepository.save(product);
        }
    }
}
