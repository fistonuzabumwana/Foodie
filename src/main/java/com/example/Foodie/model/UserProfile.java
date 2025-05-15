package com.example.Foodie.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "user_profiles")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserProfile {
    @Id
    private Long id; // Shared Primary Key with User

    @Version  // Crucial for optimistic locking
    private Integer version;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;

    @Size(max = 100)
    private String firstName;

    @Size(max = 10)
    private String phone;

    @Size(max = 100)
    private String lastName;

    @Column(name = "profile_picture_file_id")
    @Size(max = 255)
    private String profilePictureFileId;

    @Embedded
    private Address address;

    public UserProfile(User user) {
        this.user = user;
        this.id = user.getId();
        this.address = new Address(); // Initialize address to prevent NPE
    }
}