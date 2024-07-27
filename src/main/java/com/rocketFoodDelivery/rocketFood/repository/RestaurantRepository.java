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

@Repository
public interface RestaurantRepository extends JpaRepository<Restaurant, Integer> {
        Optional<Restaurant> findByUserEntityId(int id);

        List<Restaurant> findAll();

        Optional<Restaurant> findByUserEntityAndAddress(UserEntity userEntity, Address address);

        @Query(nativeQuery = true, value = "SELECT r.id, r.name, r.price_range, COALESCE(CEIL(SUM(o.restaurant_rating) / NULLIF(COUNT(o.id), 0)), 0) AS rating "
                        + "FROM restaurants r "
                        + "LEFT JOIN orders o ON r.id = o.restaurant_id "
                        + "WHERE r.id = :restaurantId "
                        + "GROUP BY r.id")
        List<Object[]> findRestaurantWithAverageRatingById(@Param("restaurantId") int restaurantId);

        @Query("SELECT r.id, r.name, r.priceRange, AVG(o.restaurantRating) as rating FROM Restaurant r " +
                        "LEFT JOIN Order o ON r.id = o.restaurant.id " +
                        "WHERE (:rating IS NULL OR AVG(o.restaurantRating) >= :rating) " +
                        "AND (:priceRange IS NULL OR r.priceRange = :priceRange) " +
                        "GROUP BY r.id, r.name, r.priceRange")
        List<Object[]> findRestaurantsByRatingAndPriceRange(@Param("rating") Integer rating,
                        @Param("priceRange") Integer priceRange);

        @Modifying
        @Transactional
        @Query(nativeQuery = true, value = "INSERT INTO restaurants (user_id, address_id, name, price_range, phone, email) "
                        + "VALUES (:userId, :addressId, :name, :priceRange, :phone, :email)")
        void saveRestaurant(@Param("userId") long userId, @Param("addressId") long addressId,
                        @Param("name") String name, @Param("priceRange") int priceRange,
                        @Param("phone") String phone, @Param("email") String email);

        @Modifying
        @Transactional
        @Query(nativeQuery = true, value = "UPDATE restaurants SET name = :name, price_range = :priceRange, phone = :phone, email = :email "
                        + "WHERE id = :restaurantId")
        void updateRestaurant(@Param("restaurantId") int restaurantId, @Param("name") String name,
                        @Param("priceRange") int priceRange, @Param("phone") String phone,
                        @Param("email") String email);

        @Query(nativeQuery = true, value = "SELECT * FROM restaurants WHERE id = :restaurantId")
        Optional<Restaurant> findRestaurantById(@Param("restaurantId") int restaurantId);

        @Modifying
        @Transactional
        @Query(nativeQuery = true, value = "DELETE FROM restaurants WHERE id = :restaurantId")
        void deleteRestaurantById(@Param("restaurantId") int restaurantId);
}
