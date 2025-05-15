package com.example.Foodie.repository;

import com.example.Foodie.model.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {
    // You might not need custom methods initially if you always access profile via User.id
    // but you could add findByUser_Id or findByUser_Username if needed.
    Optional<UserProfile> findByUserId(Long userId); // Example: Find profile by User's ID
}