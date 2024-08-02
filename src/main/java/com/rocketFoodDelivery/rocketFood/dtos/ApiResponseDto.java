package com.rocketFoodDelivery.rocketFood.dtos;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Data transfer object for API responses.
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponseDto {
    private String message;
    private Object data;

    /**
     * Converts the ApiResponseDto to a JSON string.
     *
     * @return the JSON representation of the ApiResponseDto.
     * @throws JsonProcessingException if the object cannot be converted to JSON.
     */
    public String toJson() throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(this);
    }

}
