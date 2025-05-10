package com.example.Foodie.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Entity
@Table(name = "users")
@Data // Includes Getters, Setters, toString, equals, hashCode
@NoArgsConstructor // Generates a constructor with no arguments
@AllArgsConstructor // Generates a constructor with all arguments
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password; // Will be encoded by Spring Security

    @Column(nullable = false, unique = true)
    private String email;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "role")
    private Set<String> roles; // e.g., "ROLE_USER", "ROLE_ADMIN"
}