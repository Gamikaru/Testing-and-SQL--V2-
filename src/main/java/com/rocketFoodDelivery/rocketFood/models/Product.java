package com.rocketFoodDelivery.rocketFood.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "restaurant_id", nullable = false)
    private Restaurant restaurant;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private Integer unitCost;

    public Product(int id) {
        this.id = id;
    }

    public Integer getCost() {
        return unitCost;
    }

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

        // Other builder methods...

        public Product build() {
            return new Product(id, restaurant, name, description, unitCost);
        }
    }
}
