package com.rocketFoodDelivery.rocketFood.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Data Transfer Object for restaurant details.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ApiRestaurantDto {
    private int id; // The unique identifier of the restaurant
    private String name; // The name of the restaurant

    @JsonProperty("price_range")
    private int priceRange; // The price range of the restaurant

    private int rating; // The rating of the restaurant

    @JsonProperty("address")
    private ApiAddressDto address; // The address details of the restaurant
}
