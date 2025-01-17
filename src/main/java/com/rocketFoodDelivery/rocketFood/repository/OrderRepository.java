package com.rocketFoodDelivery.rocketFood.repository;

import com.rocketFoodDelivery.rocketFood.models.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

// Annotation to indicate that this interface is a Spring Data repository
@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {

    // Method to find an Order by its ID
    Optional<Order> findById(int id);

    // Method to find all Orders by customer ID
    List<Order> findByCustomerId(int id);

    // Method to find all Orders by restaurant ID
    List<Order> findByRestaurantId(int id);

    // Method to find all Orders by courier ID
    List<Order> findByCourierId(int id);

    // Custom query to find all Orders by restaurant ID
    @Query(nativeQuery = true, value = "SELECT * FROM orders WHERE restaurant_id = :restaurantId")
    List<Order> findOrdersByRestaurantId(@Param("restaurantId") int restaurantId);

    // Custom query to delete an Order by its ID
    // The @Modifying annotation indicates that this is a modifying query (e.g.,
    // INSERT, UPDATE, DELETE)
    // The @Transactional annotation ensures that the operation is executed within a
    // transaction
    @Modifying
    @Transactional
    @Query(nativeQuery = true, value = "DELETE FROM orders WHERE id = :orderId")
    void deleteOrderById(@Param("orderId") int orderId);
}
