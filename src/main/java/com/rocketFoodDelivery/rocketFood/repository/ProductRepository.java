package com.rocketFoodDelivery.rocketFood.repository;

import com.rocketFoodDelivery.rocketFood.models.Product;
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
public interface ProductRepository extends JpaRepository<Product, Integer> {

    // Method to find a Product by its ID
    // Spring Data JPA will automatically generate the necessary SQL query
    Optional<Product> findById(int id);

    // Method to find all Products associated with a specific restaurant ID
    List<Product> findByRestaurantId(int restaurantId);

    // Custom query to find Products by restaurant ID using a native SQL query
    // The @Param annotation binds the method parameter to the named parameter in
    // the query
    @Query(nativeQuery = true, value = "SELECT * FROM products WHERE restaurant_id = :restaurantId")
    List<Product> findProductsByRestaurantId(@Param("restaurantId") int restaurantId);

    // Method to find a Product by its ID and restaurant ID
    // Useful for ensuring a Product belongs to a specific restaurant
    Optional<Product> findByIdAndRestaurantId(int id, int restaurantId);

    // Custom query to delete Products by restaurant ID using a native SQL query
    // The @Modifying and @Transactional annotations are necessary for modifying
    // queries (INSERT, UPDATE, DELETE)
    @Modifying
    @Transactional
    @Query(nativeQuery = true, value = "DELETE FROM products WHERE restaurant_id = :restaurantId")
    void deleteProductsByRestaurantId(@Param("restaurantId") int restaurantId);
}
