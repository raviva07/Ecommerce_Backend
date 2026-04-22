package com.ecommerce.repository;

import com.ecommerce.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // 🔍 Search by name
    List<Product> findByNameContainingIgnoreCase(String name);

    // 🏷️ Filter by category
    List<Product> findByCategoryIgnoreCase(String category);

    // 👁️ Active products only (customer view)
    List<Product> findByIsActiveTrue();

    // 🔍 Combined filters
    List<Product> findByIsActiveTrueAndCategoryIgnoreCase(String category);

    List<Product> findByIsActiveTrueAndNameContainingIgnoreCase(String name);

    // 👑 Admin view (all products)
    List<Product> findByIsActiveFalse();

    // 📊 Count for dashboard
    long countByIsActiveTrue();
}

