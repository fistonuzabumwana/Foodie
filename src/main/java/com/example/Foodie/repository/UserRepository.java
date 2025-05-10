package com.example.Foodie.repository;

import com.example.Foodie.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email); // Good for registration checks
    // Boolean existsByUsername(String username); // Alternative for checking
    // Boolean existsByEmail(String email);   // Alternative for checking
}