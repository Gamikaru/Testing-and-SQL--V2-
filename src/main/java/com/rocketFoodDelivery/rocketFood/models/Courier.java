package com.rocketFoodDelivery.rocketFood.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

/**
 * Represents a courier entity.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Courier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id; // Primary key for the courier

    @ManyToOne(cascade = CascadeType.REMOVE)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "user_id", unique = true, nullable = false)
    private UserEntity userEntity; // User associated with the courier

    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "address_id", nullable = false)
    private Address address; // Address of the courier

    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "courierStatus_id", nullable = false)
    private CourierStatus courierStatus; // Status of the courier

    @Column(nullable = false)
    private String phone; // Phone number of the courier

    @Column(nullable = false)
    @Email
    private String email; // Email of the courier

    @Column(columnDefinition = "boolean default true")
    private boolean active; // Whether the courier is active or not

    @Column(nullable = false)
    private String name; // Name of the courier

    /**
     * Returns the name of the courier.
     */
    public String getName() {
        return name;
    }
}
