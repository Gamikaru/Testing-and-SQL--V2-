package com.rocketFoodDelivery.rocketFood.repository;

import com.rocketFoodDelivery.rocketFood.models.Courier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

// Annotation to indicate that this interface is a Spring Data repository
@Repository
public interface CourierRepository extends JpaRepository<Courier, Integer> {

    // Method to find a Courier by its associated UserEntity ID
    Optional<Courier> findByUserEntityId(int id);
}
