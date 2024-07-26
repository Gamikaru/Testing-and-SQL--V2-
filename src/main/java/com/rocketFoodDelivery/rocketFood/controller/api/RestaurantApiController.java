package com.rocketFoodDelivery.rocketFood.controller.api;

import com.rocketFoodDelivery.rocketFood.dtos.ApiCreateRestaurantDto;
import com.rocketFoodDelivery.rocketFood.dtos.ApiRestaurantDto;
import com.rocketFoodDelivery.rocketFood.dtos.ApiResponseDto;
import com.rocketFoodDelivery.rocketFood.models.Restaurant;
import com.rocketFoodDelivery.rocketFood.service.RestaurantService;
import com.rocketFoodDelivery.rocketFood.util.ResponseBuilder;
import com.rocketFoodDelivery.rocketFood.exception.*;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/restaurants")
public class RestaurantApiController {

    private static final Logger logger = LoggerFactory.getLogger(RestaurantApiController.class);
    private final RestaurantService restaurantService;

    @Autowired
    public RestaurantApiController(RestaurantService restaurantService) {
        this.restaurantService = restaurantService;
    }

    @PostMapping
    public ResponseEntity<ApiResponseDto> createRestaurant(@Valid @RequestBody ApiCreateRestaurantDto restaurantDto,
            BindingResult bindingResult) {
        logger.info("Creating a new restaurant with data: {}", restaurantDto);

        if (bindingResult.hasErrors()) {
            List<String> errors = bindingResult.getAllErrors().stream()
                    .map(ObjectError::getDefaultMessage)
                    .collect(Collectors.toList());
            logger.warn("Validation errors occurred: {}", errors);
            return ResponseEntity.badRequest().body(new ApiResponseDto("Validation failed", errors));
        }

        return restaurantService.createRestaurant(restaurantDto)
                .map(createdRestaurant -> {
                    logger.info("Restaurant created successfully: {}", createdRestaurant);
                    return ResponseEntity.status(201)
                            .body(new ApiResponseDto("Success", createdRestaurant));
                })
                .orElseGet(() -> {
                    logger.error("Failed to create restaurant");
                    return ResponseEntity.badRequest()
                            .body(new ApiResponseDto("Failed to create restaurant", null));
                });
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponseDto> deleteRestaurant(@PathVariable int id) {
        logger.info("Deleting restaurant with id: {}", id);

        Optional<Restaurant> restaurantOptional = restaurantService.findById(id);
        if (restaurantOptional.isEmpty()) {
            logger.warn("Restaurant with id {} not found", id);
            return ResponseEntity.status(404)
                    .body(new ApiResponseDto("Resource not found", "Restaurant with id " + id + " not found"));
        }

        restaurantService.deleteRestaurant(id);
        logger.info("Restaurant with id {} deleted successfully", id);
        return ResponseEntity.ok().body(new ApiResponseDto("Restaurant deleted successfully", null));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponseDto> updateRestaurant(@PathVariable("id") int id,
            @Valid @RequestBody ApiCreateRestaurantDto restaurantUpdateData, BindingResult result) {
        logger.info("Received update request for restaurant id: {}", id);
        logger.debug("Update data: {}", restaurantUpdateData);

        Optional<Restaurant> restaurantOptional = restaurantService.findById(id);
        if (restaurantOptional.isEmpty()) {
            logger.warn("Restaurant with id {} not found", id);
            return ResponseEntity.status(404)
                    .body(new ApiResponseDto("Resource not found", "Restaurant with id " + id + " not found"));
        }

        if (result.hasErrors()) {
            List<String> errors = result.getAllErrors().stream()
                    .map(ObjectError::getDefaultMessage)
                    .collect(Collectors.toList());
            logger.warn("Validation errors occurred: {}", errors);
            return ResponseEntity.badRequest().body(new ApiResponseDto("Validation failed", errors));
        }

        return restaurantService.updateRestaurant(id, restaurantUpdateData)
                .map(updatedRestaurant -> {
                    logger.info("Updated restaurant: {}", updatedRestaurant);
                    return ResponseEntity.ok().body(new ApiResponseDto("Success", updatedRestaurant));
                })
                .orElseGet(() -> {
                    logger.error("Failed to update restaurant");
                    return ResponseEntity.badRequest()
                            .body(new ApiResponseDto("Failed to update restaurant", null));
                });
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getRestaurantById(@PathVariable int id) {
        logger.info("Fetching restaurant with id: {}", id);

        Optional<ApiRestaurantDto> restaurantWithRatingOptional = restaurantService
                .findRestaurantWithAverageRatingById(id);
        if (!restaurantWithRatingOptional.isPresent()) {
            logger.warn("Restaurant with id {} not found", id);
            throw new ResourceNotFoundException(String.format("Restaurant with id %d not found", id));
        }
        logger.info("Restaurant found: {}", restaurantWithRatingOptional.get());
        return ResponseBuilder.buildOkResponse(restaurantWithRatingOptional.get());
    }

    @GetMapping
    public ResponseEntity<Object> getAllRestaurants(@RequestParam(name = "rating", required = false) Integer rating,
            @RequestParam(name = "price_range", required = false) Integer priceRange) {
        logger.info("Fetching all restaurants with rating: {} and price range: {}", rating, priceRange);
        return ResponseBuilder
                .buildOkResponse(restaurantService.findRestaurantsByRatingAndPriceRange(rating, priceRange));
    }
}
