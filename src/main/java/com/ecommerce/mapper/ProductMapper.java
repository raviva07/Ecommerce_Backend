package com.ecommerce.mapper;

import com.ecommerce.dto.ProductRequest;
import com.ecommerce.dto.ProductResponse;
import com.ecommerce.entity.Product;

public class ProductMapper {

    // Convert DTO → Entity
    public static Product toEntity(ProductRequest request) {
        return Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .stock(request.getStock())
                .category(request.getCategory())
                .imageUrl(request.getImageUrl())
                .images(request.getImages())   // ✅ multiple images
                .rating(request.getRating())
                .isActive(true)                // default active
                .build();
    }

    // Convert Entity → DTO
    public static ProductResponse toResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .stock(product.getStock())
                .category(product.getCategory())
                .imageUrl(product.getImageUrl())
                .images(product.getImages())   // ✅ multiple images
                .rating(product.getRating())
                .isActive(product.getIsActive())
                .build();
    }
}

