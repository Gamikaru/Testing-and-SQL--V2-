package com.rocketFoodDelivery.rocketFood.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponseDto {
    private String message;
    private Object data;

    // Constructor for the specific use case in RestaurantApiController
    public ApiResponseDto(String message, ApiCreateRestaurantDto data) {
        this.message = message;
        this.data = data;
    }
}
