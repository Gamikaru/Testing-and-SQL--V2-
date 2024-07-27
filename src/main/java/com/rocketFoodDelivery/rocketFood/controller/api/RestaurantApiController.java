package com.rocketFoodDelivery.rocketFood.controller.api;

import com.rocketFoodDelivery.rocketFood.dtos.ApiCreateRestaurantDto;
import com.rocketFoodDelivery.rocketFood.dtos.ApiResponseDto;
import com.rocketFoodDelivery.rocketFood.dtos.ApiRestaurantDto;
import com.rocketFoodDelivery.rocketFood.models.Restaurant;
import com.rocketFoodDelivery.rocketFood.service.RestaurantService;
import com.rocketFoodDelivery.rocketFood.util.ResponseBuilder;
import com.rocketFoodDelivery.rocketFood.exception.*;

import jakarta.validation.Valid;

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

    private final RestaurantService restaurantService;

    @Autowired
    public RestaurantApiController(RestaurantService restaurantService) {
        this.restaurantService = restaurantService;
    }

    @PostMapping
    public ResponseEntity<ApiResponseDto> createRestaurant(@Valid @RequestBody ApiCreateRestaurantDto restaurantDto,
            BindingResult bindingResult) {
        System.out.println("Received request to create restaurant: " + restaurantDto);
        if (bindingResult.hasErrors()) {
            List<String> errors = bindingResult.getAllErrors().stream()
                    .map(ObjectError::getDefaultMessage)
                    .collect(Collectors.toList());
            System.out.println("Validation errors: " + errors);
            return ResponseEntity.badRequest().body(new ApiResponseDto("Validation failed", errors));
        }
        return restaurantService.createRestaurant(restaurantDto)
                .map(createdRestaurant -> ResponseEntity.status(201)
                        .body(new ApiResponseDto("Success", createdRestaurant)))
                .orElseGet(() -> ResponseEntity.badRequest()
                        .body(new ApiResponseDto("Failed to create restaurant", null)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponseDto> deleteRestaurant(@PathVariable int id) {
        System.out.println("Received request to delete restaurant with ID: " + id);
        Optional<Restaurant> restaurantOptional = restaurantService.findById(id);
        if (restaurantOptional.isEmpty()) {
            System.out.println("Restaurant with ID " + id + " not found");
            return ResponseEntity.status(404)
                    .body(new ApiResponseDto("Resource not found", "Restaurant with id " + id + " not found"));
        }

        restaurantService.deleteRestaurant(id);
        System.out.println("Deleted restaurant with ID: " + id);
        return ResponseEntity.ok().body(new ApiResponseDto("Restaurant deleted successfully", null));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponseDto> updateRestaurant(@PathVariable("id") int id,
            @Valid @RequestBody ApiCreateRestaurantDto restaurantUpdateData, BindingResult result) {
        System.out.println("Received request to update restaurant with ID: " + id);
        System.out.println("Update data: " + restaurantUpdateData);

        Optional<Restaurant> restaurantOptional = restaurantService.findById(id);
        if (restaurantOptional.isEmpty()) {
            System.out.println("Restaurant with ID " + id + " not found");
            return ResponseEntity.status(404)
                    .body(new ApiResponseDto("Resource not found", "Restaurant with id " + id + " not found"));
        }

        if (result.hasErrors()) {
            List<String> errors = result.getAllErrors().stream()
                    .map(ObjectError::getDefaultMessage)
                    .collect(Collectors.toList());
            System.out.println("Validation errors: " + errors);
            return ResponseEntity.badRequest().body(new ApiResponseDto("Validation failed", errors));
        }

        return restaurantService.updateRestaurant(id, restaurantUpdateData)
                .map(updatedRestaurant -> {
                    System.out.println("Updated restaurant: " + updatedRestaurant);
                    return ResponseEntity.ok().body(new ApiResponseDto("Success", updatedRestaurant));
                })
                .orElseGet(() -> {
                    System.out.println("Failed to update restaurant");
                    return ResponseEntity.badRequest()
                            .body(new ApiResponseDto("Failed to update restaurant", null));
                });
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getRestaurantById(@PathVariable int id) {
        Optional<ApiRestaurantDto> restaurantWithRatingOptional = restaurantService
                .findRestaurantWithAverageRatingById(id);
        if (!restaurantWithRatingOptional.isPresent()) {
            System.out.println("Restaurant with ID " + id + " not found");
            throw new ResourceNotFoundException(String.format("Restaurant with id %d not found", id));
        }
        System.out.println("Retrieved restaurant with ID: " + id);
        return ResponseBuilder.buildOkResponse(restaurantWithRatingOptional.get());
    }

    @GetMapping
    public ResponseEntity<Object> getAllRestaurants(@RequestParam(name = "rating", required = false) Integer rating,
            @RequestParam(name = "price_range", required = false) Integer priceRange) {
        System.out.println(
                "Received request to get all restaurants with rating: " + rating + " and price range: " + priceRange);
        return ResponseBuilder
                .buildOkResponse(restaurantService.findRestaurantsByRatingAndPriceRange(rating, priceRange));
    }
}
