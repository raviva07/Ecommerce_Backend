package com.ecommerce.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartItemResponse {

    private Long productId;
    private String productName;
    private Integer quantity;
    private java.math.BigDecimal price;
    private java.math.BigDecimal totalPrice;
}


