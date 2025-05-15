package com.example.Foodie.dto;

import com.example.Foodie.model.Address; // Assuming Address is in your model package
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileDataDto {

    @Size(max = 100, message = "First name must be less than 100 characters")
    private String firstName;

    @Size(max = 100, message = "Last name must be less than 100 characters")
    private String lastName;

    @Size(max = 10, message = "Phone must be 10 Numbers")
    private String phone;

    @Valid // Enable validation for embedded Address fields
    private AddressDto address; // Using an AddressDto

    // If Address is simple enough and directly embeddable, you could use:
    // @Valid
    // private Address address;
    // However, using a dedicated DTO for Address input is often cleaner.
}