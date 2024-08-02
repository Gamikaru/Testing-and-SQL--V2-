package com.rocketFoodDelivery.rocketFood.service;

import com.rocketFoodDelivery.rocketFood.dtos.ApiAddressDto;
import com.rocketFoodDelivery.rocketFood.dtos.ApiCreateRestaurantDto;
import com.rocketFoodDelivery.rocketFood.dtos.ApiRestaurantDto;
import com.rocketFoodDelivery.rocketFood.exception.ResourceNotFoundException;
import com.rocketFoodDelivery.rocketFood.exception.ValidationException;
import com.rocketFoodDelivery.rocketFood.models.Address;
import com.rocketFoodDelivery.rocketFood.models.Restaurant;
import com.rocketFoodDelivery.rocketFood.models.Product;
import com.rocketFoodDelivery.rocketFood.models.Employee;
import com.rocketFoodDelivery.rocketFood.models.UserEntity;
import com.rocketFoodDelivery.rocketFood.repository.AddressRepository;
import com.rocketFoodDelivery.rocketFood.repository.RestaurantRepository;
import com.rocketFoodDelivery.rocketFood.repository.EmployeeRepository;
import com.rocketFoodDelivery.rocketFood.repository.ProductRepository;
import com.rocketFoodDelivery.rocketFood.repository.UserRepository;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service class for managing restaurants.
 */
@Slf4j
@Service
public class RestaurantService {

    private final RestaurantRepository restaurantRepository;
    private final AddressRepository addressRepository;
    private final UserRepository userRepository;
    private final EmployeeRepository employeeRepository;
    private final ProductRepository productRepository;

    /**
     * Constructor for dependency injection of repositories.
     * 
     * @param restaurantRepository the repository for restaurants.
     * @param addressRepository the repository for addresses.
     * @param userRepository the repository for users.
     * @param employeeRepository the repository for employees.
     * @param productRepository the repository for products.
     */
    public RestaurantService(RestaurantRepository restaurantRepository, AddressRepository addressRepository,
            UserRepository userRepository, EmployeeRepository employeeRepository, ProductRepository productRepository) {
        this.restaurantRepository = restaurantRepository;
        this.addressRepository = addressRepository;
        this.userRepository = userRepository;
        this.employeeRepository = employeeRepository;
        this.productRepository = productRepository;
    }

    /**
     * Creates a new restaurant.
     * 
     * @param restaurantDto the data transfer object containing restaurant details.
     * @return the created restaurant as an ApiRestaurantDto.
     */
    @Transactional
    public ApiRestaurantDto createRestaurant(@Valid ApiCreateRestaurantDto restaurantDto) {
        log.info("Validating restaurant data: {}", restaurantDto);
        validateAddress(restaurantDto);
        UserEntity userEntity = validateUser(restaurantDto.getUserId());

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

    /**
     * Retrieves a list of restaurants based on rating and price range.
     * 
     * @param rating the rating filter.
     * @param priceRange the price range filter.
     * @return a list of restaurants as ApiRestaurantDto.
     */
    public List<ApiRestaurantDto> getRestaurants(Integer rating, Integer priceRange) {
        log.info("Fetching restaurants with rating: {} and price range: {}", rating, priceRange);

        List<Object[]> restaurantData = restaurantRepository.findRestaurantsByRatingAndPriceRange(rating, priceRange);
        log.info("Retrieved restaurant data: {}",
                restaurantData.stream().map(Arrays::toString).collect(Collectors.joining(", ")));

        return restaurantData.stream()
                .map(this::mapToApiRestaurantDtoFromData)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves a restaurant by its ID.
     * 
     * @param id the ID of the restaurant.
     * @return the restaurant as an ApiRestaurantDto.
     */
    public ApiRestaurantDto getRestaurantById(Integer id) {
        log.info("Fetching restaurant by ID: {}", id);

        Restaurant restaurant = restaurantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant with id " + id + " not found"));
        log.info("Converting entity to DTO: {}", restaurant);
        return mapToApiRestaurantDto(restaurant);
    }

    /**
     * Updates an existing restaurant.
     * 
     * @param id the ID of the restaurant.
     * @param restaurantDto the data transfer object containing updated restaurant details.
     * @return the updated restaurant as an ApiRestaurantDto.
     */
    @Transactional
    public ApiRestaurantDto updateRestaurant(Integer id, @Valid ApiCreateRestaurantDto restaurantDto) {
        log.info("Validating updated restaurant data: {}", restaurantDto);
        validateAddress(restaurantDto);
        UserEntity userEntity = validateUser(restaurantDto.getUserId());

        Restaurant existingRestaurant = restaurantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant with id " + id + " not found"));

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

    /**
     * Deletes a restaurant by its ID.
     * 
     * @param id the ID of the restaurant.
     * @return the deleted restaurant as an ApiRestaurantDto.
     */
    @Transactional
    public ApiRestaurantDto deleteRestaurant(Integer id) {
        log.info("Fetching restaurant by ID: {}", id);

        Restaurant restaurant = restaurantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant with id " + id + " not found"));

        // Delete related products first
        List<Product> products = productRepository.findByRestaurantId(restaurant.getId());
        for (Product product : products) {
            productRepository.deleteById(product.getId());
        }

        // Fetch and delete employees related to the restaurant
        List<Employee> employees = employeeRepository.findEmployeesByRestaurantId(restaurant.getId());
        for (Employee employee : employees) {
            employeeRepository.deleteById(employee.getId());
        }

        // Now delete the restaurant
        log.info("Deleting restaurant entity: {}", restaurant);
        restaurantRepository.deleteRestaurantById(restaurant.getId());

        // Return the deleted restaurant details as DTO
        return mapToApiRestaurantDto(restaurant);
    }

    /**
     * Validates if the address exists in the DTO.
     * 
     * @param restaurantDto the restaurant DTO.
     */
    private void validateAddress(ApiCreateRestaurantDto restaurantDto) {
        if (restaurantDto.getAddress() == null) {
            log.error("Address is required");
            throw new ValidationException("Address is required");
        }
    }

    /**
     * Validates if the user exists in the database.
     * 
     * @param userId the user ID.
     * @return the UserEntity.
     */
    private UserEntity validateUser(Integer userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ValidationException("User not found"));
    }

    /**
     * Maps an ApiAddressDto to an Address entity.
     * 
     * @param addressDto the address DTO.
     * @return the Address entity.
     */
    private Address mapToAddressEntity(ApiAddressDto addressDto) {
        return Address.builder()
                .streetAddress(addressDto.getStreetAddress())
                .city(addressDto.getCity())
                .postalCode(addressDto.getPostalCode())
                .build();
    }

    /**
     * Maps an ApiCreateRestaurantDto to a Restaurant entity.
     * 
     * @param restaurantDto the restaurant DTO.
     * @param address the Address entity.
     * @param userEntity the UserEntity.
     * @return the Restaurant entity.
     */
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

    /**
     * Maps a Restaurant entity to an ApiRestaurantDto.
     * 
     * @param restaurant the Restaurant entity.
     * @return the ApiRestaurantDto.
     */
    private ApiRestaurantDto mapToApiRestaurantDto(Restaurant restaurant) {
        return ApiRestaurantDto.builder()
                .id(restaurant.getId())
                .name(restaurant.getName())
                .priceRange(restaurant.getPriceRange())
                .rating(calculateRestaurantRating(restaurant))
                .address(mapToApiAddressDto(restaurant.getAddress())) // Include address in DTO
                .build();
    }

    /**
     * Maps restaurant data from a database query to an ApiRestaurantDto.
     * 
     * @param data the restaurant data from the query.
     * @return the ApiRestaurantDto.
     */
    private ApiRestaurantDto mapToApiRestaurantDtoFromData(Object[] data) {
        log.info("Mapping restaurant data to DTO: {}", Arrays.toString(data));
        Address address = addressRepository.findById((Integer) data[4]).orElse(null);
        ApiRestaurantDto restaurantDto = ApiRestaurantDto.builder()
                .id((Integer) data[0])
                .name((String) data[1])
                .priceRange((Integer) data[2])
                .rating(((BigDecimal) data[3]).intValue())
                .address(mapToApiAddressDto(address))
                .build();
        log.info("Mapped restaurant DTO: {}", restaurantDto);
        return restaurantDto;
    }

    /**
     * Calculates the rating for a restaurant.
     * 
     * @param restaurant the Restaurant entity.
     * @return the calculated rating.
     */
    private int calculateRestaurantRating(Restaurant restaurant) {
        log.info("Calculating rating for restaurant: {}", restaurant.getId());
        List<Object[]> ratingData = restaurantRepository.findRestaurantWithAverageRatingById(restaurant.getId());
        int rating = ratingData.isEmpty() ? 0 : ((BigDecimal) ratingData.get(0)[3]).intValue();
        log.info("Calculated rating: {}", rating);
        return rating;
    }

    /**
     * Maps an Address entity to an ApiAddressDto.
     * 
     * @param address the Address entity.
     * @return the ApiAddressDto.
     */
    private ApiAddressDto mapToApiAddressDto(Address address) {
        if (address == null) {
            return null;
        }
        ApiAddressDto addressDto = ApiAddressDto.builder()
                .id(address.getId())
                .streetAddress(address.getStreetAddress())
                .city(address.getCity())
                .postalCode(address.getPostalCode())
                .build();
        log.info("Mapped address DTO: {}", addressDto);
        return addressDto;
    }
}
