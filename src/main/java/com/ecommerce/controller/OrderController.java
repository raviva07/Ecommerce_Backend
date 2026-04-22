package com.ecommerce.controller;

import com.ecommerce.constants.ApiMessages;
import com.ecommerce.dto.OrderResponse;
import com.ecommerce.entity.Order;
import com.ecommerce.entity.OrderStatus;
import com.ecommerce.entity.User;
import com.ecommerce.mapper.OrderMapper;
import com.ecommerce.response.ApiResponse;
import com.ecommerce.service.OrderService;
import com.ecommerce.service.UserService;

import lombok.RequiredArgsConstructor;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final UserService userService;

    // CUSTOMER: Create order
    @PostMapping
    public ApiResponse<OrderResponse> createOrder(Authentication authentication) {

        User user = userService.getUserByEmail(authentication.getName());

        Order order = orderService.createOrder(user);

        return ApiResponse.<OrderResponse>builder()
                .success(true)
                .message(ApiMessages.ORDER_CREATED)
                .data(OrderMapper.toResponse(order))
                .build();
    }

    // CUSTOMER: Get my orders
    @GetMapping("/my")
    public ApiResponse<List<OrderResponse>> getMyOrders(Authentication authentication) {

        User user = userService.getUserByEmail(authentication.getName());

        List<Order> orders = orderService.getUserOrders(user);

        return ApiResponse.<List<OrderResponse>>builder()
                .success(true)
                .message(ApiMessages.ORDER_FETCHED)
                .data(orders == null ? Collections.emptyList()
                        : orders.stream().map(OrderMapper::toResponse).toList())
                .build();
    }

    // ADMIN: Get all orders (🔥 TEST DEPENDS ON THIS)
    @GetMapping("/all")
    public ApiResponse<List<OrderResponse>> getAllOrders() {

        List<Order> orders = orderService.getAllOrders();

        return ApiResponse.<List<OrderResponse>>builder()
                .success(true)
                .message(ApiMessages.ORDER_FETCHED)
                .data(orders == null ? Collections.emptyList()
                        : orders.stream().map(OrderMapper::toResponse).toList())
                .build();
    }

    // Get order by ID
    @GetMapping("/{id}")
    public ApiResponse<OrderResponse> getOrderById(@PathVariable Long id) {

        Order order = orderService.getOrderById(id);

        return ApiResponse.<OrderResponse>builder()
                .success(true)
                .message(ApiMessages.ORDER_FETCHED)
                .data(OrderMapper.toResponse(order))
                .build();
    }

    // Update status
    @PutMapping("/{id}/status")
    public ApiResponse<OrderResponse> updateOrderStatus(
            @PathVariable Long id,
            @RequestParam OrderStatus status) {

        Order order = orderService.updateOrderStatus(id, status);

        return ApiResponse.<OrderResponse>builder()
                .success(true)
                .message(ApiMessages.ORDER_UPDATED)
                .data(OrderMapper.toResponse(order))
                .build();
    }

    // CUSTOMER: Cancel own order
    @PutMapping("/{id}/cancel")
    public ApiResponse<OrderResponse> cancelOrder(
            @PathVariable Long id,
            Authentication authentication) {

        User user = userService.getUserByEmail(authentication.getName());

        Order order = orderService.cancelOrder(id, user);

        return ApiResponse.<OrderResponse>builder()
                .success(true)
                .message(ApiMessages.ORDER_CANCELLED)
                .data(OrderMapper.toResponse(order))
                .build();
    }

    // ADMIN: Cancel any order
    @PutMapping("/{id}/admin-cancel")
    public ApiResponse<OrderResponse> adminCancelOrder(@PathVariable Long id) {

        Order order = orderService.adminCancelOrder(id);

        return ApiResponse.<OrderResponse>builder()
                .success(true)
                .message(ApiMessages.ORDER_CANCELLED)
                .data(OrderMapper.toResponse(order))
                .build();
    }
}
