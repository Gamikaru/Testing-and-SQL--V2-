package com.rocketFoodDelivery.rocketFood.models;

import jakarta.persistence.*;
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
 * Represents a product order entity.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "product_orders", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "product_id", "order_id" })
})
public class ProductOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id; // Primary key for the product order

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonBackReference
    private Product product; // Product associated with the order

    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonBackReference
    private Order order; // Order associated with the product

    @Min(1)
    private Integer product_quantity; // Quantity of the product in the order

    @Min(0)
    private Integer product_unit_cost; // Cost per unit of the product in the order

    /**
     * Validates before persisting or updating the entity.
     */
    @PrePersist
    @PreUpdate
    private void validateBeforePersist() {
        if (!productBelongsToRestaurant()) {
            throw new IllegalArgumentException(
                    "ProductOrder instance is not valid: product does not belong to the restaurant");
        }
    }

    /**
     * Checks if the product belongs to the restaurant of the order.
     */
    private boolean productBelongsToRestaurant() {
        return product != null && order != null && product.getRestaurant().getId() == order.getRestaurant().getId();
    }
}
