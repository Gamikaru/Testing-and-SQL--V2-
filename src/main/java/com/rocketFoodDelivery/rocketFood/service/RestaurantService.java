package com.rocketFoodDelivery.rocketFood.service;

import com.rocketFoodDelivery.rocketFood.models.UserEntity;
import com.rocketFoodDelivery.rocketFood.models.Address;
import com.rocketFoodDelivery.rocketFood.dtos.ApiAddressDto;
import com.rocketFoodDelivery.rocketFood.repository.AddressRepository;
import com.rocketFoodDelivery.rocketFood.repository.UserRepository;
import com.rocketFoodDelivery.rocketFood.repository.RestaurantRepository;

import com.rocketFoodDelivery.rocketFood.dtos.ApiCreateRestaurantDto;
import com.rocketFoodDelivery.rocketFood.dtos.ApiRestaurantDto;
import com.rocketFoodDelivery.rocketFood.models.Restaurant;
import com.rocketFoodDelivery.rocketFood.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import java.util.Optional;

@Service
public class RestaurantService {
    private final RestaurantRepository restaurantRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final ProductOrderRepository productOrderRepository;
    private final UserRepository userRepository;
    private final AddressRepository addressRepository;

    @Autowired
    public RestaurantService(
            RestaurantRepository restaurantRepository,
            ProductRepository productRepository,
            OrderRepository orderRepository,
            ProductOrderRepository productOrderRepository,
            UserRepository userRepository,
            AddressRepository addressRepository) {
        this.restaurantRepository = restaurantRepository;
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
        this.productOrderRepository = productOrderRepository;
        this.userRepository = userRepository;
        this.addressRepository = addressRepository;
    }

    public List<Restaurant> findAllRestaurants() {
        return restaurantRepository.findAll();
    }

    /**
     * Retrieves a restaurant with its details, including the average rating, based
     * on the provided restaurant ID.
     *
     * @param id The unique identifier of the restaurant to retrieve.
     * @return An Optional containing a RestaurantDto with details such as id, name,
     *         price range, and average rating.
     *         If the restaurant with the given id is not found, an empty Optional
     *         is returned.
     *
     * @see RestaurantRepository#findRestaurantWithAverageRatingById(int) for the
     *      raw query details from the repository.
     */
    public Optional<ApiRestaurantDto> findRestaurantWithAverageRatingById(int id) {
        List<Object[]> restaurant = restaurantRepository.findRestaurantWithAverageRatingById(id);

        if (!restaurant.isEmpty()) {
            Object[] row = restaurant.get(0);
            int restaurantId = (int) row[0];
            String name = (String) row[1];
            int priceRange = (int) row[2];
            double rating = (row[3] != null) ? ((BigDecimal) row[3]).setScale(1, RoundingMode.HALF_UP).doubleValue()
                    : 0.0;
            int roundedRating = (int) Math.ceil(rating);
            ApiRestaurantDto restaurantDto = new ApiRestaurantDto(restaurantId, name, priceRange, roundedRating);
            return Optional.of(restaurantDto);
        } else {
            return Optional.empty();
        }
    }

    /**
     * Finds restaurants based on the provided rating and price range.
     *
     * @param rating     The rating for filtering the restaurants.
     * @param priceRange The price range for filtering the restaurants.
     * @return A list of ApiRestaurantDto objects representing the selected
     *         restaurants.
     *         Each object contains the restaurant's ID, name, price range, and a
     *         rounded-up average rating.
     */
    public List<ApiRestaurantDto> findRestaurantsByRatingAndPriceRange(Integer rating, Integer priceRange) {
        List<Object[]> restaurants = restaurantRepository.findRestaurantsByRatingAndPriceRange(rating, priceRange);

        List<ApiRestaurantDto> restaurantDtos = new ArrayList<>();

        for (Object[] row : restaurants) {
            int restaurantId = (int) row[0];
            String name = (String) row[1];
            int range = (int) row[2];
            double avgRating = (row[3] != null) ? ((BigDecimal) row[3]).setScale(1, RoundingMode.HALF_UP).doubleValue()
                    : 0.0;
            int roundedAvgRating = (int) Math.ceil(avgRating);
            restaurantDtos.add(new ApiRestaurantDto(restaurantId, name, range, roundedAvgRating));
        }

        return restaurantDtos;
    }

    /**
     * Creates a new restaurant and returns its information.
     *
     * @param restaurantDto The data for the new restaurant.
     * @return An Optional containing the created restaurant's information as an
     *         ApiCreateRestaurantDto,
     *         or Optional.empty() if the user with the provided user ID does not
     *         exist or if an error occurs during creation.
     */
    @Transactional
    public Optional<ApiCreateRestaurantDto> createRestaurant(ApiCreateRestaurantDto restaurantDto) {
        Optional<UserEntity> userOptional = userRepository.findById(restaurantDto.getUserId());
        if (userOptional.isEmpty()) {
            return Optional.empty();
        }
        UserEntity userEntity = userOptional.get();

        Address address = new Address();
        address.setStreetAddress(restaurantDto.getAddress().getStreetAddress());
        address.setCity(restaurantDto.getAddress().getCity());
        address.setPostalCode(restaurantDto.getAddress().getPostalCode());
        address = addressRepository.save(address);

        Restaurant restaurant = new Restaurant(userEntity, address, restaurantDto.getName(),
                restaurantDto.getPriceRange(), restaurantDto.getPhone(), restaurantDto.getEmail());
        restaurantRepository.save(restaurant);
        restaurantDto.setId(restaurant.getId());
        return Optional.of(restaurantDto);
    }

    /**
     * Finds a restaurant by its ID.
     *
     * @param id The ID of the restaurant to retrieve.
     * @return An Optional containing the restaurant with the specified ID,
     *         or Optional.empty() if no restaurant is found.
     */
    public Optional<Restaurant> findById(int id) {
        return restaurantRepository.findById(id);
    }

    /**
     * Updates an existing restaurant by ID with the provided data.
     *
     * @param id                   The ID of the restaurant to update.
     * @param updatedRestaurantDto The updated data for the restaurant.
     * @return An Optional containing the updated restaurant's information as an
     *         ApiCreateRestaurantDto,
     *         or Optional.empty() if the restaurant with the specified ID is not
     *         found or if an error occurs during the update.
     */
    @Transactional
    public Optional<ApiCreateRestaurantDto> updateRestaurant(int id, ApiCreateRestaurantDto updatedRestaurantDto) {
        System.out.println("Received update request for restaurant id: " + id);
        System.out.println("Update data: " + updatedRestaurantDto);

        Optional<Restaurant> restaurantOptional = restaurantRepository.findById(id);
        if (restaurantOptional.isEmpty()) {
            System.out.println("Restaurant with id " + id + " not found");
            return Optional.empty();
        }

        Restaurant restaurant = restaurantOptional.get();
        restaurant.setName(updatedRestaurantDto.getName());
        restaurant.setPriceRange(updatedRestaurantDto.getPriceRange());
        restaurant.setPhone(updatedRestaurantDto.getPhone());
        restaurant.setEmail(updatedRestaurantDto.getEmail());

        ApiAddressDto updatedAddressDto = updatedRestaurantDto.getAddress();
        if (updatedAddressDto != null) {
            Address existingAddress = restaurant.getAddress();
            existingAddress.setStreetAddress(updatedAddressDto.getStreetAddress());
            existingAddress.setCity(updatedAddressDto.getCity());
            existingAddress.setPostalCode(updatedAddressDto.getPostalCode());
            addressRepository.save(existingAddress);
        }

        Restaurant updatedRestaurant = restaurantRepository.save(restaurant);
        System.out.println("Updated restaurant: " + updatedRestaurant);

        updatedRestaurantDto.setId(updatedRestaurant.getId());
        return Optional.of(updatedRestaurantDto);
    }

    /**
     * Deletes a restaurant along with its associated data, including its product
     * orders, orders and products.
     *
     * @param restaurantId The ID of the restaurant to delete.
     */
    @Transactional
    public void deleteRestaurant(int restaurantId) {
        restaurantRepository.deleteRestaurantById(restaurantId);
    }
}
