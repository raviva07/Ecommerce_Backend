package com.ecommerce.mapper;

import com.ecommerce.dto.*;
import com.ecommerce.entity.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class OrderMapper {

    public static OrderItemResponse toItemResponse(OrderItem item) {
        return OrderItemResponse.builder()
                .productId(item.getProduct().getId())
                .productName(item.getProduct().getName())
                .quantity(item.getQuantity())
                .price(BigDecimal.valueOf(item.getPrice())) // ✅ FIX
                .build();
    }

    public static OrderResponse toResponse(Order order) {

        List<OrderItemResponse> items = order.getItems() == null
                ? new ArrayList<>()
                : order.getItems().stream()
                    .map(OrderMapper::toItemResponse) // ✅ reuse method
                    .toList();

        return OrderResponse.builder()
                .orderId(order.getId())
                .userId(order.getUser().getId())
                .items(items)
                .totalAmount(BigDecimal.valueOf(order.getTotalAmount())) // ✅ FIX
                .status(order.getStatus())
                .createdAt(order.getCreatedAt())
                .build();
    }
    
}
