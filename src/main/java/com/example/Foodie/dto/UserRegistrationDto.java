package com.example.Foodie.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
// Add custom validator for matching passwords if you want client-side or more complex validation
// For now, we'll handle password matching in the controller or service

public class UserRegistrationDto {

    @NotEmpty(message = "Username cannot be empty")
    @Size(min = 3, max = 20, message = "Username must be between 3 and 20 characters")
    private String username;

    @NotEmpty(message = "Email cannot be empty")
    @Email(message = "Please provide a valid email address")
    private String email;

    @NotEmpty(message = "Password cannot be empty")
    @Size(min = 6, message = "Password must be at least 6 characters long")
    private String password;

    @NotEmpty(message = "Confirm password cannot be empty")
    private String confirmPassword;

    // Getters and Setters (Lombok @Data can also be used here)
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getConfirmPassword() { return confirmPassword; }
    public void setConfirmPassword(String confirmPassword) { this.confirmPassword = confirmPassword; }
}