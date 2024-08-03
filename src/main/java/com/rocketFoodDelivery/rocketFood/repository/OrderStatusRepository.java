package com.rocketFoodDelivery.rocketFood.repository;

import com.rocketFoodDelivery.rocketFood.models.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

// Annotation to indicate that this interface is a Spring Data repository
@Repository
public interface OrderStatusRepository extends JpaRepository<OrderStatus, Integer> {

    // Method to find an OrderStatus by its ID
    // Spring Data JPA will automatically generate the necessary SQL query
    Optional<OrderStatus> findById(int id);

    // Method to find an OrderStatus by its name
    // This allows searching for an OrderStatus using its name
    Optional<OrderStatus> findByName(String name);
}
