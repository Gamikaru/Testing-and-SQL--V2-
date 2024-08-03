package com.rocketFoodDelivery.rocketFood.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonBackReference;

/**
 * Represents an order entity.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id; // Primary key for the order

    @ManyToOne
    @JoinColumn(name = "restaurant_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonBackReference
    private Restaurant restaurant; // Restaurant associated with the order

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonBackReference
    private Customer customer; // Customer associated with the order

    @ManyToOne
    @JoinColumn(name = "status_id", nullable = false)
    private OrderStatus order_status; // Status of the order

    @ManyToOne
    @JoinColumn(name = "courier_id")
    @JsonManagedReference
    private Courier courier; // Courier associated with the order

    @Column(nullable = false)
    @Min(1)
    @Max(5)
    private int restaurant_rating; // Rating of the restaurant

    /**
     * Returns the restaurant rating.
     */
    public int getRestaurantRating() {
        return this.restaurant_rating;
    }
}
