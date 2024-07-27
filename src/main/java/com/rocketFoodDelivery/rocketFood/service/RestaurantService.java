package com.rocketFoodDelivery.rocketFood.service;

import com.rocketFoodDelivery.rocketFood.dtos.ApiAddressDto;
import com.rocketFoodDelivery.rocketFood.dtos.ApiCreateRestaurantDto;
import com.rocketFoodDelivery.rocketFood.dtos.ApiRestaurantDto;
import com.rocketFoodDelivery.rocketFood.models.Address;
import com.rocketFoodDelivery.rocketFood.models.Restaurant;
import com.rocketFoodDelivery.rocketFood.models.UserEntity;
import com.rocketFoodDelivery.rocketFood.repository.AddressRepository;
import com.rocketFoodDelivery.rocketFood.repository.RestaurantRepository;
import com.rocketFoodDelivery.rocketFood.repository.UserRepository;
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
    private final UserRepository userRepository;
    private final AddressRepository addressRepository;

    @Autowired
    public RestaurantService(
            RestaurantRepository restaurantRepository,
            UserRepository userRepository,
            AddressRepository addressRepository) {
        this.restaurantRepository = restaurantRepository;
        this.userRepository = userRepository;
        this.addressRepository = addressRepository;
    }

    public List<Restaurant> findAllRestaurants() {
        return restaurantRepository.findAll();
    }

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
            restaurantDtos.add(ApiRestaurantDto.builder()
                    .id(restaurantId)
                    .name(name)
                    .priceRange(range)
                    .rating(roundedAvgRating)
                    .build());
        }

        return restaurantDtos;
    }

    @Transactional
    public Optional<ApiCreateRestaurantDto> createRestaurant(ApiCreateRestaurantDto restaurantDto) {
        Optional<UserEntity> userOptional = userRepository.findById(restaurantDto.getUserId());
        if (userOptional.isEmpty()) {
            return Optional.empty();
        }
        UserEntity userEntity = userOptional.get();

        Address address = Address.builder()
                .streetAddress(restaurantDto.getAddress().getStreetAddress())
                .city(restaurantDto.getAddress().getCity())
                .postalCode(restaurantDto.getAddress().getPostalCode())
                .build();
        address = addressRepository.save(address);

        Restaurant restaurant = Restaurant.builder()
                .userEntity(userEntity)
                .address(address)
                .name(restaurantDto.getName())
                .priceRange(restaurantDto.getPriceRange())
                .phone(restaurantDto.getPhone())
                .email(restaurantDto.getEmail())
                .build();
        restaurant = restaurantRepository.save(restaurant); // Updated this line
        restaurantDto.setId(restaurant.getId());

        System.out.println("Persisted Restaurant: " + restaurant);
        System.out.println("Persisted Address: " + address);

        return Optional.of(restaurantDto);
    }

    public Optional<Restaurant> findById(int id) {
        return restaurantRepository.findById(id);
    }

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

    @Transactional
    public void deleteRestaurant(int restaurantId) {
        System.out.println("Deleting restaurant with id: " + restaurantId);
        restaurantRepository.deleteRestaurantById(restaurantId);
        System.out.println("Deleted restaurant with id: " + restaurantId);
    }
}
