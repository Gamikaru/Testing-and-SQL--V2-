package com.rocketFoodDelivery.rocketFood.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;



@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "employees")
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @OneToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "user_id", unique = true, nullable = false)
    private UserEntity userEntity;

    @OneToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "address_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE) // Ensure cascading delete
    private Address address;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String phone;
}
