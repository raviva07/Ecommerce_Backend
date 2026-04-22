package com.ecommerce.repository;

import com.ecommerce.entity.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @Test
    void testSave() {

        Product product = Product.builder()
                .name("Laptop")
                .price(BigDecimal.valueOf(70000))
                .category("Electronics") // ✅ REQUIRED
                .stock(10)
                .description("Laptop desc")
                .isActive(true)
                .build();

        Product saved = productRepository.save(product);

        assertNotNull(saved.getId());
    }

    @Test
    void testFindByIsActiveTrue() {

        Product product = Product.builder()
                .name("Phone")
                .price(BigDecimal.valueOf(20000))
                .category("Electronics") // ✅ REQUIRED
                .stock(5)
                .description("Phone desc")
                .isActive(true)
                .build();

        productRepository.save(product);

        List<Product> products = productRepository.findByIsActiveTrue();

        assertFalse(products.isEmpty());
    }

    @Test
    void testSearchProduct() {

        Product product = Product.builder()
                .name("iPhone")
                .description("Good phone")
                .price(BigDecimal.valueOf(10000))
                .stock(10)
                .category("Electronics") // ✅ REQUIRED
                .isActive(true)
                .build();

        productRepository.save(product);

        List<Product> result =
                productRepository.findByIsActiveTrueAndNameContainingIgnoreCase("iphone");

        assertFalse(result.isEmpty());
    }
}
