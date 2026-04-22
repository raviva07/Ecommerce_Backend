package com.ecommerce.mapper;

import com.ecommerce.dto.*;
import com.ecommerce.entity.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

public class CartMapper {

    public static CartItemResponse toItemResponse(CartItem item) {
        return CartItemResponse.builder()
                .productId(item.getProduct().getId())
                .productName(item.getProduct().getName())
                .quantity(item.getQuantity())
                .price(BigDecimal.valueOf(item.getPrice())) // ✅ FIX
                .totalPrice(BigDecimal.valueOf(item.getPrice() * item.getQuantity())) // ✅ FIX
                .build();
    }

    public static CartResponse toResponse(Cart cart) {

        List<CartItemResponse> items = cart.getItems() == null
                ? List.of()
                : cart.getItems()
                    .stream()
                    .map(CartMapper::toItemResponse) // ✅ FIXED METHOD
                    .collect(Collectors.toList());

        return CartResponse.builder()
                .cartId(cart.getId())
                .userId(cart.getUser().getId()) // 🔥 IMPORTANT FIX
                .items(items)
                .totalPrice(BigDecimal.valueOf(cart.getTotalPrice())) // ✅ FIX
                .build();
    }
}

