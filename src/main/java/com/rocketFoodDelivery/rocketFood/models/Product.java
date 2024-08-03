package com.rocketFoodDelivery.rocketFood.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

/**
 * Represents a product entity.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id; // Primary key for the product

    @ManyToOne
    @JoinColumn(name = "restaurant_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Restaurant restaurant; // Restaurant associated with the product

    @Column(nullable = false)
    private String name; // Name of the product

    @Column(nullable = false)
    private String description; // Description of the product

    @Column(nullable = false)
    private Integer unitCost; // Cost per unit of the product

    /**
     * Constructor to create a product with a given ID.
     */
    public Product(int id) {
        this.id = id;
    }

    /**
     * Returns the cost of the product.
     */
    public Integer getCost() {
        return unitCost;
    }

    /**
     * Product builder class.
     */
    public static class ProductBuilder {
        private int id;
        private Restaurant restaurant;
        private String name;
        private String description;
        private Integer unitCost;

        public ProductBuilder cost(Integer unitCost) {
            this.unitCost = unitCost;
            return this;
        }

        public Product build() {
            return new Product(id, restaurant, name, description, unitCost);
        }
    }
}
