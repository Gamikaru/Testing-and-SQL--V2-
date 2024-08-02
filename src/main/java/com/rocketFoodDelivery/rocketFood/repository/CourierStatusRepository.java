package com.rocketFoodDelivery.rocketFood.repository;

import com.rocketFoodDelivery.rocketFood.models.CourierStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// Annotation to indicate that this interface is a Spring Data repository
@Repository
public interface CourierStatusRepository extends JpaRepository<CourierStatus, Integer> {
    // Method to find a CourierStatus by its name
    CourierStatus findByName(String name);
}
