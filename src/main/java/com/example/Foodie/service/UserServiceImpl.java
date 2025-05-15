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
    private final UserProfileService userProfileService;

    @Autowired
    public UserServiceImpl(UserRepository userRepository,
                           PasswordEncoder passwordEncoder,
                           UserProfileService userProfileService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userProfileService = userProfileService;
    }

    @Override
    @Transactional
    public User registerNewUser(User userFromDto) throws Exception {
        if (userRepository.findByUsername(userFromDto.getUsername()).isPresent()) {
            throw new Exception("Username already exists: " + userFromDto.getUsername());
        }
        if (userRepository.findByEmail(userFromDto.getEmail()).isPresent()) {
            throw new Exception("Email already exists: " + userFromDto.getEmail());
        }

        User newUser = new User();
        newUser.setUsername(userFromDto.getUsername());
        newUser.setEmail(userFromDto.getEmail());
        newUser.setPassword(passwordEncoder.encode(userFromDto.getPassword()));
        newUser.setRoles(Set.of("ROLE_USER"));

        User savedUser = userRepository.save(newUser);
        userProfileService.createDefaultProfile(savedUser);
        return savedUser;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }
}