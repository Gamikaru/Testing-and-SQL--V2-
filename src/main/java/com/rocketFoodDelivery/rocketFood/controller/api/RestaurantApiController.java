package com.rocketFoodDelivery.rocketFood.controller.api;

import com.rocketFoodDelivery.rocketFood.dtos.ApiCreateRestaurantDto;
import com.rocketFoodDelivery.rocketFood.dtos.ApiRestaurantDto;
import com.rocketFoodDelivery.rocketFood.exception.ValidationException;
import com.rocketFoodDelivery.rocketFood.exception.ResourceNotFoundException;
import com.rocketFoodDelivery.rocketFood.service.RestaurantService;
import com.rocketFoodDelivery.rocketFood.util.ResponseBuilder;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/restaurants")
@Validated
public class RestaurantApiController {

    private final RestaurantService restaurantService;

    public RestaurantApiController(RestaurantService restaurantService) {
        this.restaurantService = restaurantService;
    }

    @PostMapping
    public ResponseEntity<?> createRestaurant(@RequestBody @Valid ApiCreateRestaurantDto restaurantDto) {
        log.info("Received request to create restaurant: {}", restaurantDto);

        try {
            log.info("Validating restaurant data: {}", restaurantDto);
            ApiRestaurantDto savedRestaurant = restaurantService.createRestaurant(restaurantDto);
            log.info("Restaurant created successfully: {}", savedRestaurant);
            return ResponseBuilder.buildResponse("Success", savedRestaurant, 201);
        } catch (ValidationException ex) {
            log.error("Validation error: {}", ex.getMessage());
            return ResponseBuilder.buildBadRequestResponse("Invalid or missing parameters");
        } catch (Exception ex) {
            log.error("Exception occurred while saving restaurant: {}", ex.getMessage(), ex);
            return ResponseBuilder.buildErrorResponse("Internal server error: " + ex.getMessage(), 500);
        }
    }

    @GetMapping
    public ResponseEntity<?> getRestaurants(@RequestParam(required = false) Integer rating,
            @RequestParam(name = "price_range", required = false) Integer priceRange) {
        log.info("Received GET request to fetch restaurants with rating: {} and price range: {}", rating, priceRange);

        if (priceRange == null) {
            log.warn("Price range is null");
        } else {
            log.info("Price range is: {}", priceRange);
        }

        try {
            List<ApiRestaurantDto> restaurants = restaurantService.getRestaurants(rating, priceRange);
            log.info("Fetched restaurants: {}", restaurants);
            return ResponseBuilder.buildResponse("Success", restaurants, 200);
        } catch (Exception ex) {
            log.error("Exception occurred while fetching restaurants: {}", ex.getMessage());
            return ResponseBuilder.buildErrorResponse("Internal server error", 500);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getRestaurantById(@PathVariable Integer id) {
        log.info("Fetching restaurant with ID: {}", id);

        try {
            log.info("Calling service to fetch restaurant by ID.");
            ApiRestaurantDto restaurant = restaurantService.getRestaurantById(id);
            log.info("Restaurant details: {}", restaurant);
            return ResponseBuilder.buildResponse("Success", restaurant, 200);
        } catch (ResourceNotFoundException ex) {
            log.error("Restaurant not found with ID: {}", id);
            return ResponseBuilder.buildNotFoundResponse("Restaurant with id " + id + " not found");
        } catch (Exception ex) {
            log.error("Exception occurred while fetching restaurant: {}", ex.getMessage());
            return ResponseBuilder.buildErrorResponse("Internal server error", 500);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateRestaurant(@PathVariable Integer id,
            @RequestBody @Valid ApiCreateRestaurantDto restaurantDto) {
        log.info("Received request to update restaurant: {}", restaurantDto);

        try {
            log.info("Validating updated restaurant data: {}", restaurantDto);
            ApiRestaurantDto updatedRestaurant = restaurantService.updateRestaurant(id, restaurantDto);
            log.info("Restaurant updated successfully: {}", updatedRestaurant);
            return ResponseBuilder.buildResponse("Success", updatedRestaurant, 200);
        } catch (ValidationException ex) {
            log.error("Validation error: {}", ex.getMessage());
            return ResponseBuilder.buildBadRequestResponse("Validation failed: " + ex.getMessage());
        } catch (ResourceNotFoundException ex) {
            log.error("Restaurant not found with ID: {}", id);
            return ResponseBuilder.buildNotFoundResponse("Restaurant with id " + id + " not found");
        } catch (Exception ex) {
            log.error("Exception occurred while updating restaurant: {}", ex.getMessage());
            return ResponseBuilder.buildErrorResponse("Internal server error", 500);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRestaurant(@PathVariable Integer id) {
        log.info("Deleting restaurant with ID: {}", id);

        try {
            log.info("Calling service to delete restaurant.");
            ApiRestaurantDto deletedRestaurant = restaurantService.deleteRestaurant(id);
            log.info("Restaurant deleted successfully: {}", id);
            return ResponseBuilder.buildResponse("Success", deletedRestaurant, 200);
        } catch (ResourceNotFoundException ex) {
            log.error("Restaurant not found with ID: {}", id);
            return ResponseBuilder.buildNotFoundResponse("Restaurant with id " + id + " not found");
        } catch (Exception ex) {
            log.error("Exception occurred while deleting restaurant: {}", ex.getMessage());
            return ResponseBuilder.buildErrorResponse("Internal server error", 500);
        }
    }
}
