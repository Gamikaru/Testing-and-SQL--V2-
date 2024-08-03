package com.rocketFoodDelivery.rocketFood.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a courier status entity.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "courierStatuses")
public class CourierStatus {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id; // Primary key for the courier status

    @Column(nullable = false)
    private String name; // Name of the courier status
}
