package com.rocketFoodDelivery.rocketFood.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents an address entity.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "addresses")
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id; // Primary key for the address

    @Column(name = "street_address", nullable = false)
    private String streetAddress; // Street address of the location

    @Column(nullable = false)
    private String city; // City of the address

    @Column(name = "postal_code", nullable = false)
    private String postalCode; // Postal code of the address

    /**
     * Returns a string representation of the address.
     */
    @Override
    public String toString() {
        return streetAddress + ", " + city + ", " + postalCode;
    }
}
