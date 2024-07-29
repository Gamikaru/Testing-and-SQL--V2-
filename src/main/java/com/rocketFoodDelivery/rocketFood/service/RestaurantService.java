package com.rocketFoodDelivery.rocketFood.service;

import com.rocketFoodDelivery.rocketFood.dtos.ApiAddressDto;
import com.rocketFoodDelivery.rocketFood.dtos.ApiCreateRestaurantDto;
import com.rocketFoodDelivery.rocketFood.dtos.ApiRestaurantDto;
import com.rocketFoodDelivery.rocketFood.exception.ResourceNotFoundException;
import com.rocketFoodDelivery.rocketFood.exception.ValidationException;
import com.rocketFoodDelivery.rocketFood.models.Address;
import com.rocketFoodDelivery.rocketFood.models.Restaurant;
import com.rocketFoodDelivery.rocketFood.models.UserEntity;
import com.rocketFoodDelivery.rocketFood.repository.AddressRepository;
import com.rocketFoodDelivery.rocketFood.repository.RestaurantRepository;
import com.rocketFoodDelivery.rocketFood.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class RestaurantService {

    private final RestaurantRepository restaurantRepository;
    private final AddressRepository addressRepository;
    private final UserRepository userRepository;

    public RestaurantService(RestaurantRepository restaurantRepository, AddressRepository addressRepository,
            UserRepository userRepository) {
        this.restaurantRepository = restaurantRepository;
        this.addressRepository = addressRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public ApiRestaurantDto createRestaurant(@Valid ApiCreateRestaurantDto restaurantDto) {
        log.info("Validating restaurant data: {}", restaurantDto);

        if (restaurantDto.getAddress() == null) {
            log.error("Address is required");
            throw new ValidationException("Address is required");
        }

        UserEntity userEntity = userRepository.findById(restaurantDto.getUserId())
                .orElseThrow(() -> new ValidationException("User not found"));

        log.info("Converting DTO to entity: {}", restaurantDto);
        Address address = mapToAddressEntity(restaurantDto.getAddress());
        Restaurant restaurant = mapToRestaurantEntity(restaurantDto, address, userEntity);

        log.info("Saving restaurant entity: {}", restaurant);
        Address savedAddress = addressRepository.save(address);
        restaurant.setAddress(savedAddress);
        Restaurant savedRestaurant = restaurantRepository.save(restaurant);

        log.info("Converting saved entity to DTO: {}", savedRestaurant);
        return mapToApiRestaurantDto(savedRestaurant);
    }

    public List<ApiRestaurantDto> getRestaurants(Integer rating, Integer priceRange) {
        log.info("Fetching restaurants with rating: {} and price range: {}", rating, priceRange);

        List<Object[]> restaurantData = restaurantRepository.findRestaurantsByRatingAndPriceRange(rating, priceRange);

        log.info("Converting entities to DTOs: {}", restaurantData);
        return restaurantData.stream()
                .map(this::mapToApiRestaurantDtoFromData)
                .collect(Collectors.toList());
    }

    public ApiRestaurantDto getRestaurantById(Integer id) {
        log.info("Fetching restaurant by ID: {}", id);

        Optional<Restaurant> optionalRestaurant = restaurantRepository.findById(id);
        if (optionalRestaurant.isEmpty()) {
            log.error("Restaurant with id {} not found", id);
            throw new ResourceNotFoundException("Restaurant with id " + id + " not found");
        }

        Restaurant restaurant = optionalRestaurant.get();
        log.info("Converting entity to DTO: {}", restaurant);
        return mapToApiRestaurantDto(restaurant);
    }

    @Transactional
    public ApiRestaurantDto updateRestaurant(Integer id, @Valid ApiCreateRestaurantDto restaurantDto) {
        log.info("Validating updated restaurant data: {}", restaurantDto);

        if (restaurantDto.getAddress() == null) {
            log.error("Address is required");
            throw new ValidationException("Address is required");
        }

        UserEntity userEntity = userRepository.findById(restaurantDto.getUserId())
                .orElseThrow(() -> new ValidationException("User not found"));

        log.info("Fetching existing restaurant by ID: {}", id);
        Optional<Restaurant> optionalRestaurant = restaurantRepository.findById(id);
        if (optionalRestaurant.isEmpty()) {
            log.error("Restaurant with id {} not found", id);
            throw new ResourceNotFoundException("Restaurant with id " + id + " not found");
        }

        Restaurant existingRestaurant = optionalRestaurant.get();
        Address updatedAddress = mapToAddressEntity(restaurantDto.getAddress());

        log.info("Updating entity fields with DTO data: {}", restaurantDto);
        existingRestaurant.setName(restaurantDto.getName());
        existingRestaurant.setPhone(restaurantDto.getPhone());
        existingRestaurant.setEmail(restaurantDto.getEmail());
        existingRestaurant.setPriceRange(restaurantDto.getPriceRange());
        existingRestaurant.setAddress(updatedAddress);
        existingRestaurant.setUserEntity(userEntity);

        log.info("Saving updated restaurant entity: {}", existingRestaurant);
        addressRepository.save(updatedAddress);
        Restaurant updatedRestaurant = restaurantRepository.save(existingRestaurant);

        log.info("Converting updated entity to DTO: {}", updatedRestaurant);
        return mapToApiRestaurantDto(updatedRestaurant);
    }

    @Transactional
    public void deleteRestaurant(Integer id) {
        log.info("Fetching restaurant by ID: {}", id);

        Optional<Restaurant> optionalRestaurant = restaurantRepository.findById(id);
        if (optionalRestaurant.isEmpty()) {
            log.error("Restaurant with id {} not found", id);
            throw new ResourceNotFoundException("Restaurant with id " + id + " not found");
        }

        Restaurant restaurant = optionalRestaurant.get();

        log.info("Deleting restaurant entity: {}", restaurant);
        restaurantRepository.delete(restaurant);
    }

    private Address mapToAddressEntity(ApiAddressDto addressDto) {
        return Address.builder()
                .streetAddress(addressDto.getStreetAddress())
                .city(addressDto.getCity())
                .postalCode(addressDto.getPostalCode())
                .build();
    }

    private Restaurant mapToRestaurantEntity(ApiCreateRestaurantDto restaurantDto, Address address,
            UserEntity userEntity) {
        return Restaurant.builder()
                .name(restaurantDto.getName())
                .phone(restaurantDto.getPhone())
                .email(restaurantDto.getEmail())
                .priceRange(restaurantDto.getPriceRange())
                .address(address)
                .userEntity(userEntity)
                .build();
    }

    private ApiRestaurantDto mapToApiRestaurantDto(Restaurant restaurant) {
        return ApiRestaurantDto.builder()
                .id(restaurant.getId())
                .name(restaurant.getName())
                .priceRange(restaurant.getPriceRange())
                .rating(calculateRestaurantRating(restaurant))
                .build();
    }

    private ApiRestaurantDto mapToApiRestaurantDtoFromData(Object[] data) {
        return ApiRestaurantDto.builder()
                .id((Integer) data[0])
                .name((String) data[1])
                .priceRange((Integer) data[2])
                .rating((Integer) data[3])
                .build();
    }

    private int calculateRestaurantRating(Restaurant restaurant) {
        List<Object[]> ratingData = restaurantRepository.findRestaurantWithAverageRatingById(restaurant.getId());
        return ratingData.isEmpty() ? 0 : (int) ratingData.get(0)[3];
    }
}
