// package com.rocketFoodDelivery.rocketFood.controller.api;

// import com.rocketFoodDelivery.rocketFood.dtos.ApiCreateRestaurantDto;
// import com.rocketFoodDelivery.rocketFood.dtos.ApiRestaurantDto;
// import com.rocketFoodDelivery.rocketFood.dtos.ApiResponseDto;
// import com.rocketFoodDelivery.rocketFood.service.RestaurantService;
// import com.rocketFoodDelivery.rocketFood.util.ResponseBuilder;
// import com.rocketFoodDelivery.rocketFood.exception.*;

// import jakarta.validation.Valid;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.http.ResponseEntity;
// import org.springframework.validation.BindingResult;
// import org.springframework.web.bind.annotation.*;

// import java.util.Optional;

// @RestController
// @RequestMapping("/api/restaurants")
// public class RestaurantApiController {

//     private final RestaurantService restaurantService;

//     @Autowired
//     public RestaurantApiController(RestaurantService restaurantService) {
//         this.restaurantService = restaurantService;
//     }

//     @PostMapping
//     public ResponseEntity<ApiResponseDto> createRestaurant(@RequestBody ApiCreateRestaurantDto restaurantDto) {
//         return restaurantService.createRestaurant(restaurantDto)
//                 .map(createdRestaurant -> ResponseEntity.status(201).body(new ApiResponseDto("Success", createdRestaurant)))
//                 .orElseGet(() -> ResponseEntity.badRequest().body(new ApiResponseDto("Failed to create restaurant", null)));
//     }

//     // TODO

//     /**
//      * Deletes a restaurant by ID.
//      *
//      * @param id The ID of the restaurant to delete.
//      * @return ResponseEntity with a success message, or a ResourceNotFoundException
//      *         if the restaurant is not found.
//      */
//     @DeleteMapping("/api/restaurants/{id}")
//     public ResponseEntity<Object> deleteRestaurant(@PathVariable int id) {
//         return null; // TODO return proper object
//     }

//     // TODO

//     /**
//      * Updates an existing restaurant by ID.
//      *
//      * @param id                   The ID of the restaurant to update.
//      * @param restaurantUpdateData The updated data for the restaurant.
//      * @param result               BindingResult for validation.
//      * @return ResponseEntity with the updated restaurant's data
//      */
//     // @PutMapping("/api/restaurants/{id}")
//     // public ResponseEntity<Object> updateRestaurant(@PathVariable("id") int id,
//     // @Valid @RequestBody ApiCreateRestaurantDto restaurantUpdateData,
//     // BindingResult result) {
//     // return null; // TODO return proper object
//     // }

//     // /**
//     // * Retrieves details for a restaurant, including its average rating, based on
//     // * the provided restaurant ID.
//     // *
//     // * @param id The unique identifier of the restaurant to retrieve.
//     // * @return ResponseEntity with HTTP 200 OK if the restaurant is found, HTTP
//     // 404
//     // * Not Found otherwise.
//     // *
//     // * @see RestaurantService#findRestaurantWithAverageRatingById(int) for details
//     // * on retrieving restaurant information.
//     // */
//     // @GetMapping("/api/restaurants/{id}")
//     // public ResponseEntity<Object> getRestaurantById(@PathVariable int id) {
//     // Optional<ApiRestaurantDto> restaurantWithRatingOptional = restaurantService
//     // .findRestaurantWithAverageRatingById(id);
//     // if (!restaurantWithRatingOptional.isPresent())
//     // throw new ResourceNotFoundException(String.format("Restaurant with id %d not
//     // found", id));
//     // return ResponseBuilder.buildOkResponse(restaurantWithRatingOptional.get());
//     // }

//     // /**
//     // * Returns a list of restaurants given a rating and price range
//     // *
//     // * @param rating integer from 1 to 5 (optional)
//     // * @param priceRange integer from 1 to 3 (optional)
//     // * @return A list of restaurants that match the specified criteria
//     // *
//     // * @see RestaurantService#findRestaurantsByRatingAndPriceRange(Integer,
//     // Integer)
//     // * for details on retrieving restaurant information.
//     // */

//     // @GetMapping("/api/restaurants")
//     // public ResponseEntity<Object> getAllRestaurants(
//     // @RequestParam(name = "rating", required = false) Integer rating,
//     // @RequestParam(name = "price_range", required = false) Integer priceRange) {
//     // return ResponseBuilder
//     // .buildOkResponse(restaurantService.findRestaurantsByRatingAndPriceRange(rating,
//     // priceRange));
//     // }
// }

package com.rocketFoodDelivery.rocketFood.controller.api;

import com.rocketFoodDelivery.rocketFood.dtos.ApiCreateRestaurantDto;
import com.rocketFoodDelivery.rocketFood.dtos.ApiRestaurantDto;
import com.rocketFoodDelivery.rocketFood.dtos.ApiResponseDto;
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
        if (bindingResult.hasErrors()) {
            List<String> errors = bindingResult.getAllErrors().stream()
                    .map(ObjectError::getDefaultMessage)
                    .collect(Collectors.toList());
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
        Optional<Restaurant> restaurantOptional = restaurantService.findById(id);
        if (restaurantOptional.isEmpty()) {
            return ResponseEntity.status(404)
                    .body(new ApiResponseDto("Resource not found", "Restaurant with id " + id + " not found"));
        }

        restaurantService.deleteRestaurant(id);
        return ResponseEntity.ok().body(new ApiResponseDto("Restaurant deleted successfully", null));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponseDto> updateRestaurant(@PathVariable("id") int id,
            @Valid @RequestBody ApiCreateRestaurantDto restaurantUpdateData, BindingResult result) {

        // Log the incoming data
        System.out.println("Received update request for restaurant id: " + id);
        System.out.println("Update data: " + restaurantUpdateData);

        // Check for validation errors
        if (result.hasErrors()) {
            List<String> errors = result.getAllErrors().stream()
                    .map(ObjectError::getDefaultMessage)
                    .collect(Collectors.toList());
            // Log validation errors
            errors.forEach(System.out::println);
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
        if (!restaurantWithRatingOptional.isPresent())
            throw new ResourceNotFoundException(String.format("Restaurant with id %d not found", id));
        return ResponseBuilder.buildOkResponse(restaurantWithRatingOptional.get());
    }

    @GetMapping
    public ResponseEntity<Object> getAllRestaurants(@RequestParam(name = "rating", required = false) Integer rating,
            @RequestParam(name = "price_range", required = false) Integer priceRange) {
        return ResponseBuilder
                .buildOkResponse(restaurantService.findRestaurantsByRatingAndPriceRange(rating, priceRange));
    }
}
