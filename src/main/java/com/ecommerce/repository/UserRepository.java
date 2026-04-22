package com.ecommerce.repository;

import com.ecommerce.entity.User;
import com.ecommerce.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // 🔐 Authentication
    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    // 👑 Admin operations
    List<User> findByRole(Role role);

    long countByRole(Role role);
}
