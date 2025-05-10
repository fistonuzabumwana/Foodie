package com.example.Foodie.service;

import com.example.Foodie.model.User;
import com.example.Foodie.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Set;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public User registerNewUser(User user) throws Exception {
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new Exception("Username already exists: " + user.getUsername());
        }
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new Exception("Email already exists: " + user.getEmail());
        }

        User newUser = new User();
        newUser.setUsername(user.getUsername());
        newUser.setEmail(user.getEmail());
        newUser.setPassword(passwordEncoder.encode(user.getPassword())); // Encode password
        newUser.setRoles(Set.of("ROLE_USER")); // Default role for new users

        return userRepository.save(newUser);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
}