package com.example.Foodie.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddressDto {
    @Size(max = 255)
    private String streetAddress;

    @Size(max = 100)
    private String sector;

    @Size(max = 100)
    private String district;

    @Size(max = 100)
    private String country;
}