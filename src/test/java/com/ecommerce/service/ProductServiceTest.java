package com.ecommerce.service;

import com.ecommerce.entity.Product;
import com.ecommerce.entity.User;
import com.ecommerce.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProductServiceTest {

    @InjectMocks
    private ProductService productService;

    @Mock
    private ProductRepository productRepository;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAddProduct() {

        Product product = Product.builder()
                .name("Phone")
                .price(BigDecimal.valueOf(5000))
                .build();

        User admin = new User();

        when(productRepository.save(any(Product.class)))
                .thenReturn(product);

        Product saved = productService.addProduct(product, admin);

        assertNotNull(saved);
        assertEquals("Phone", saved.getName());
        verify(productRepository, times(1)).save(product);
    }
}
