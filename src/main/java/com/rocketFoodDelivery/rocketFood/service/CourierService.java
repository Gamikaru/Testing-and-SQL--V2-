package com.rocketFoodDelivery.rocketFood.service;

import com.rocketFoodDelivery.rocketFood.repository.CourierRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service // This annotation indicates that the class is a service component in Spring
public class CourierService { // This service is a placeholder for operations related to couriers.

    // Dependency injection of CourierRepository
    private final CourierRepository courierRepository;

    // Constructor for dependency injection
    @Autowired
    public CourierService(CourierRepository courierRepository) {
        this.courierRepository = courierRepository;
    }
}
