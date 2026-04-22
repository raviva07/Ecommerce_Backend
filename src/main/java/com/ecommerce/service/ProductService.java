package com.ecommerce.service;

import com.ecommerce.entity.Product;
import com.ecommerce.entity.User;
import com.ecommerce.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public Product addProduct(Product product, User admin) {
        product.setCreatedBy(admin);
        product.setIsActive(true);
        return productRepository.save(product);
    }

    public Product updateProduct(Long id, Product updated) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        product.setName(updated.getName());
        product.setDescription(updated.getDescription());
        product.setPrice(updated.getPrice());
        product.setStock(updated.getStock());
        product.setCategory(updated.getCategory());

        if (updated.getImageUrl() != null && !updated.getImageUrl().isBlank()) {
            product.setImageUrl(updated.getImageUrl());
        }
        if (updated.getImages() != null && !updated.getImages().isEmpty()) {
            product.setImages(updated.getImages());
        }

        return productRepository.save(product);
    }

    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));
        product.setIsActive(false);
        productRepository.save(product);
    }

    public List<Product> getAllProducts() {
        return productRepository.findByIsActiveTrue();
    }

    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));
    }

    public List<Product> searchProducts(String keyword) {
        return productRepository.findByIsActiveTrueAndNameContainingIgnoreCase(keyword);
    }
}
