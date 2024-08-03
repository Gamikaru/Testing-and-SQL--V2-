package com.rocketFoodDelivery.rocketFood.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

/**
 * Represents an employee entity.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "employees")
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id; // Primary key for the employee

    @OneToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "user_id", unique = true, nullable = false)
    private UserEntity userEntity; // User associated with the employee

    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE) // Ensure cascading delete
    @JoinColumn(name = "address_id", nullable = false)
    private Address address; // Address of the employee

    @Column(nullable = false)
    private String email; // Email of the employee

    @Column(nullable = false)
    private String phone; // Phone number of the employee
}
