package com.rocketFoodDelivery.rocketFood.dtos;

import lombok.Getter;
import lombok.Setter;
import lombok.Builder;

/**
 * Data Transfer Object for restaurant rating.
 */
@Getter
@Setter
@Builder
public class ApiRestaurantRatingDto {
    private int restaurantRating; // The rating of the restaurant
}
