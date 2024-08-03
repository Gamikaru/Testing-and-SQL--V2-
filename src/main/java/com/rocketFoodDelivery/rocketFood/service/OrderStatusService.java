package com.rocketFoodDelivery.rocketFood.service;

import com.rocketFoodDelivery.rocketFood.models.OrderStatus;
import com.rocketFoodDelivery.rocketFood.repository.OrderStatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OrderStatusService {

    private final OrderStatusRepository orderStatusRepository;

    // Constructor for dependency injection of OrderStatusRepository
    @Autowired
    public OrderStatusService(OrderStatusRepository orderStatusRepository) {
        this.orderStatusRepository = orderStatusRepository;
    }

    /**
     * Retrieves all order statuses.
     *
     * @return a list of all order statuses.
     */
    public List<OrderStatus> getAllOrderStatuses() {
        return orderStatusRepository.findAll();
    }

    /**
     * Finds an order status by its ID.
     *
     * @param id the ID of the order status.
     * @return an optional containing the order status if found, or empty if not
     *         found.
     */
    public Optional<OrderStatus> findById(int id) {
        return orderStatusRepository.findById(id);
    }
}
