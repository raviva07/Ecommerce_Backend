package com.ecommerce.dto;

import lombok.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartResponse {

    private Long cartId;
    private Long userId;
    private List<CartItemResponse> items;
    private java.math.BigDecimal totalPrice;
}


