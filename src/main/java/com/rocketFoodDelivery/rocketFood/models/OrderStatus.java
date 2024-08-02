package com.rocketFoodDelivery.rocketFood.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents an order status entity.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "order_statuses", uniqueConstraints = @UniqueConstraint(columnNames = "name"))
public class OrderStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id; // Primary key for the order status

    @Column(nullable = false)
    private String name; // Name of the order status
}
