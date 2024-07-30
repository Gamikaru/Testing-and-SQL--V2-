package com.rocketFoodDelivery.rocketFood.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "restaurants")
public class Restaurant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity userEntity;

    @ManyToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "address_id", unique = true, nullable = false)
    private Address address;

    @Column(nullable = false)
    private String name;

    @Column(name = "price_range", nullable = false)
    @Min(1)
    @Max(3)
    private int priceRange;

    @Column(nullable = false)
    private String phone;

    @Column(nullable = false)
    private String email;

    public Restaurant(int id) {
        this.id = id;
    }
}
