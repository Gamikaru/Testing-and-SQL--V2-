package com.rocketFoodDelivery.rocketFood.repository;

import com.rocketFoodDelivery.rocketFood.models.Restaurant;
import com.rocketFoodDelivery.rocketFood.models.UserEntity;
import com.rocketFoodDelivery.rocketFood.models.Address;
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
public interface RestaurantRepository extends JpaRepository<Restaurant, Integer> {

        // Method to find a Restaurant by the UserEntity ID
        Optional<Restaurant> findByUserEntityId(int userId);

        // Custom query to find Restaurants by rating and price range using a native SQL
        // query
        // The @Param annotation binds the method parameters to the named parameters in
        // the query
        @Query(nativeQuery = true, value = "SELECT r.id, r.name, r.price_range, " +
                        "COALESCE(CEIL(SUM(o.restaurant_rating) / NULLIF(COUNT(o.id), 0)), 0) AS rating, r.address_id "
                        +
                        "FROM restaurants r " +
                        "LEFT JOIN orders o ON r.id = o.restaurant_id " +
                        "GROUP BY r.id, r.name, r.price_range, r.address_id " +
                        "HAVING (:priceRange IS NULL OR r.price_range = :priceRange) " +
                        "AND (:rating IS NULL OR rating = :rating)")
        List<Object[]> findRestaurantsByRatingAndPriceRange(@Param("rating") Integer rating,
                        @Param("priceRange") Integer priceRange);

        // Custom query to find a Restaurant by its ID and calculate its average rating
        @Query(nativeQuery = true, value = "SELECT r.id, r.name, r.price_range, " +
                        "COALESCE(CEIL(SUM(o.restaurant_rating) / NULLIF(COUNT(o.id), 0)), 0) AS rating, r.address_id "
                        +
                        "FROM restaurants r " +
                        "LEFT JOIN orders o ON r.id = o.restaurant_id " +
                        "WHERE r.id = :restaurantId " +
                        "GROUP BY r.id, r.name, r.price_range, r.address_id")
        List<Object[]> findRestaurantWithAverageRatingById(@Param("restaurantId") int restaurantId);

        // Custom query to insert a new Restaurant using a native SQL query
        // The @Modifying and @Transactional annotations are necessary for modifying
        // queries (INSERT, UPDATE, DELETE)
        @Modifying
        @Transactional
        @Query(nativeQuery = true, value = "INSERT INTO restaurants (user_id, address_id, name, price_range, phone, email) "
                        +
                        "VALUES (:userId, :addressId, :name, :priceRange, :phone, :email)")
        void saveRestaurant(@Param("userId") long userId, @Param("addressId") long addressId,
                        @Param("name") String name, @Param("priceRange") int priceRange,
                        @Param("phone") String phone, @Param("email") String email);

        // Custom query to update an existing Restaurant using a native SQL query
        @Modifying
        @Transactional
        @Query(nativeQuery = true, value = "UPDATE restaurants SET name = :name, price_range = :priceRange, phone = :phone, email = :email "
                        +
                        "WHERE id = :restaurantId")
        void updateRestaurant(@Param("restaurantId") int restaurantId, @Param("name") String name,
                        @Param("priceRange") int priceRange, @Param("phone") String phone,
                        @Param("email") String email);

        // Custom query to find a Restaurant by its ID using a native SQL query
        @Query(nativeQuery = true, value = "SELECT * FROM restaurants WHERE id = :restaurantId")
        Optional<Restaurant> findRestaurantById(@Param("restaurantId") int restaurantId);

        // Custom query to delete a Restaurant by its ID using a native SQL query
        @Modifying
        @Transactional
        @Query(nativeQuery = true, value = "DELETE FROM restaurants WHERE id = :restaurantId")
        void deleteRestaurantById(@Param("restaurantId") int restaurantId);

        // Method to find a Restaurant by both UserEntity and Address
        Optional<Restaurant> findByUserEntityAndAddress(UserEntity userEntity, Address address);
}
