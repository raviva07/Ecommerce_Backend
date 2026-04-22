package com.ecommerce.dto;

import com.ecommerce.entity.OrderStatus;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderResponse {

    private Long orderId;
    private Long userId;
    private List<OrderItemResponse> items;
    private java.math.BigDecimal totalAmount;
    private OrderStatus status;
    private java.time.LocalDateTime createdAt;
}
