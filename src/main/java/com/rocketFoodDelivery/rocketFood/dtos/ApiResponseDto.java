package com.rocketFoodDelivery.rocketFood.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponseDto {
    private String message;
    private Object data;

    @Override
    public String toString() {
        return "{\"message\":\"" + message + "\", \"data\":\"" + data + "\"}";
    }
}
