package com.warba.banking.customer.repository.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
public class Address {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Address label is required")
    private String label;

    @NotBlank(message = "Country name is required")
    private String country;

    @NotBlank(message = "City name is required")
    private String city;

    @NotBlank(message = "Street name is required")
    private String street;

    @Column(name = "building_number")
    private Integer buildingNumber;
}
