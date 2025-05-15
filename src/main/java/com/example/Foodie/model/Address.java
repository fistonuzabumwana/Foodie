package com.example.Foodie.model; // Or your chosen package

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Embeddable // Marks this class as embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Address {

    @Size(max = 255)
    private String streetAddress;

    @Size(max = 100)
    private String sector;

    @Size(max = 100)
    private String district;

    @Size(max = 100)
    private String country; // You could default this to "Rwanda" or make it a selection
}