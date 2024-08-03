package com.rocketFoodDelivery.rocketFood.repository;

import com.rocketFoodDelivery.rocketFood.models.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

// Annotation to indicate that this interface is a Spring Data repository
@Repository
public interface UserRepository extends JpaRepository<UserEntity, Integer> {

    // Method to find a UserEntity by its ID
    Optional<UserEntity> findById(int id);

    // Method to find a UserEntity by its email
    Optional<UserEntity> findByEmail(String email);

    // Method to find all UserEntities and order them by ID in descending order
    List<UserEntity> findAllByOrderByIdDesc();
}
