package com.rocketFoodDelivery.rocketFood.repository;

import com.rocketFoodDelivery.rocketFood.models.ProductOrder;
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
public interface ProductOrderRepository extends JpaRepository<ProductOrder, Integer> {

    // Custom query to delete product orders by order ID
    // The @Modifying annotation indicates that this is a modifying query (e.g.,
    // INSERT, UPDATE, DELETE)
    // The @Transactional annotation ensures that the operation is executed within a
    // transaction
    @Modifying
    @Transactional
    @Query(nativeQuery = true, value = "DELETE FROM product_orders WHERE order_id = :orderId")
    void deleteProductOrdersByOrderId(@Param("orderId") int orderId);

    // Method to find a ProductOrder by its ID
    Optional<ProductOrder> findById(int id);

    // Method to find a ProductOrder by its order ID and product ID
    Optional<ProductOrder> findByOrderIdAndProductId(int orderId, int productId);

    // Method to find all ProductOrders by order ID
    List<ProductOrder> findByOrderId(int orderId);

    // Method to find all ProductOrders by product ID
    List<ProductOrder> findByProductId(int productId);
}
