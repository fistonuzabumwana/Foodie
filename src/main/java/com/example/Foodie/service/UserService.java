package com.example.Foodie.service;

import com.example.Foodie.model.User;
import java.util.Optional;

public interface UserService {
    User registerNewUser(User user) throws Exception; // Could throw an exception if username/email exists
    Optional<User> findByUsername(String username);

    Optional<User> findById(Long id);

    // Add other user-related methods if needed
}