// package com.rocketFoodDelivery.rocketFood.repository;

// import static org.junit.jupiter.api.Assertions.assertTrue;

// import java.util.List;
// import java.util.Optional;

// import org.junit.jupiter.api.Test;
// import org.junit.jupiter.api.BeforeEach;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
// import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
// import org.springframework.test.context.ActiveProfiles;

// import com.rocketFoodDelivery.rocketFood.models.Restaurant;

// @DataJpaTest
// @AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
// @ActiveProfiles("test")
// public class RestaurantRepositoryTest {

//     @Autowired
//     private RestaurantRepository restaurantRepository;

//     @BeforeEach
//     public void setup() {
//         Restaurant restaurant = new Restaurant();
//         restaurant.setId(1);
//         restaurant.setName("Test Restaurant");
//         restaurant.setPriceRange(2);
//         restaurantRepository.save(restaurant);

//         // Add some orders with ratings to this restaurant
//         Order order = new Order();
//         order.setRestaurant(restaurant);
//         order.setRestaurantRating(4);
//         orderRepository.save(order);
//     }

//     @Test
//     public void testFindRestaurantWithAverageRatingById() {
//         int restaurantId = 1; // Ensure this ID exists in the database
//         List<Object[]> result = restaurantRepository.findRestaurantWithAverageRatingById(restaurantId);
//         assertTrue(result.size() > 0, "Expected to find at least one result");
//     }
// }
