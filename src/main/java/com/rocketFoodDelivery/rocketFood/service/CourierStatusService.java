package com.rocketFoodDelivery.rocketFood.service;

import com.rocketFoodDelivery.rocketFood.models.CourierStatus;
import com.rocketFoodDelivery.rocketFood.repository.CourierStatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service // This annotation indicates that the class is a service component in Spring
public class CourierStatusService {

    // Dependency injection of CourierStatusRepository
    private final CourierStatusRepository courierStatusRepository;

    // Constructor for dependency injection
    @Autowired
    public CourierStatusService(CourierStatusRepository courierStatusRepository) {
        this.courierStatusRepository = courierStatusRepository;
    }

    /**
     * Find a CourierStatus by its name.
     *
     * @param name The name of the CourierStatus.
     * @return The CourierStatus object if found, or null if not.
     */
    public CourierStatus findByName(String name) {
        return courierStatusRepository.findByName(name);
    }
}
