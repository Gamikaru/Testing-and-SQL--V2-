package com.rocketFoodDelivery.rocketFood.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonBackReference;

import java.util.List;

/**
 * Represents a restaurant entity.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "restaurants")
public class Restaurant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id; // Primary key for the restaurant

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false) // Ensure user_id is not nullable
    private UserEntity userEntity; // User associated with the restaurant

    @ManyToOne
    @JoinColumn(name = "address_id", unique = true, nullable = false) // Ensure address_id is unique and not nullable
    private Address address; // Address of the restaurant

    @Column(nullable = false)
    private String name; // Name of the restaurant

    @Column(name = "price_range", nullable = false)
    @Min(1)
    @Max(3)
    private int priceRange; // Price range of the restaurant

    @Column(nullable = false)
    private String phone; // Phone number of the restaurant

    @Column(nullable = false)
    private String email; // Email of the restaurant

    @Column(nullable = false)
    private boolean active; // Whether the restaurant is active or not

    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Product> products; // Products associated with the restaurant

    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonBackReference
    private List<Order> orders; // Orders associated with the restaurant

    /**
     * Constructor to create a restaurant with a given ID.
     */
    public Restaurant(int id) {
        this.id = id;
    }

    /**
     * Returns the list of orders associated with the restaurant.
     */
    @JsonBackReference
    public List<Order> getOrders() {
        return orders;
    }
}
