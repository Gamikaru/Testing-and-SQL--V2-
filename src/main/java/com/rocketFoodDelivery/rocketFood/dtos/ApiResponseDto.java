package com.rocketFoodDelivery.rocketFood.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;

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
        try {
            return "{\"message\":\"" + message + "\", \"data\":" + new ObjectMapper().writeValueAsString(data) + "}";
        } catch (JsonProcessingException e) {
            // Handle the exception
            return "{\"message\":\"" + message + "\", \"data\":\"" + data + "\"}";
        }
    }

}
