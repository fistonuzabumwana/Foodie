package com.example.Foodie; // Or com.example.Foodie.config

import com.example.Foodie.model.User;
import com.example.Foodie.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Check if admin user already exists
        if (!userRepository.findByUsername("admin").isPresent()) {
            User adminUser = new User();
            adminUser.setUsername("admin");
            adminUser.setPassword(passwordEncoder.encode("adminpass")); // Encode the password
            adminUser.setEmail("admin@example.com");
            adminUser.setRoles(Set.of("ROLE_ADMIN", "ROLE_USER")); // Admin can also be a user
            userRepository.save(adminUser);
            System.out.println("Created default admin user with username 'admin' and password 'adminpass'");
        }

        // You can also create a default regular user for testing
        if (!userRepository.findByUsername("user").isPresent()) {
            User regularUser = new User();
            regularUser.setUsername("user");
            regularUser.setPassword(passwordEncoder.encode("userpass")); // Encode the password
            regularUser.setEmail("user@example.com");
            regularUser.setRoles(Set.of("ROLE_USER"));
            userRepository.save(regularUser);
            System.out.println("Created default regular user with username 'user' and password 'userpass'");
        }
    }
}