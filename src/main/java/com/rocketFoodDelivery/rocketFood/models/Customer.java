package com.rocketFoodDelivery.rocketFood.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import com.fasterxml.jackson.annotation.JsonBackReference;

import java.util.List;

/**
 * Represents a customer entity.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "customers")
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id; // Primary key for the customer

    @OneToOne
    @JoinColumn(name = "user_id", unique = true, nullable = false)
    private UserEntity userEntity; // User associated with the customer

    @ManyToOne
    @JoinColumn(name = "address_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Address address; // Address of the customer

    @Column(columnDefinition = "boolean default true")
    private boolean active; // Whether the customer is active or not

    @Column(nullable = false)
    private String phone; // Phone number of the customer

    @Email
    @Column(nullable = false)
    private String email; // Email of the customer

    @Column(nullable = false)
    private String name; // Name of the customer

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonBackReference
    private List<Order> orders; // Orders associated with the customer

    /**
     * Returns the name of the customer.
     */
    public String getName() {
        return name;
    }

    /**
     * Constructor to create a customer with a given ID.
     */
    public Customer(int id) {
        this.id = id;
    }

    /**
     * Returns the list of orders associated with the customer.
     */
    @JsonBackReference
    public List<Order> getOrders() {
        return orders;
    }
}
