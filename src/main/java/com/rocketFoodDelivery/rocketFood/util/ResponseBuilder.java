package com.rocketFoodDelivery.rocketFood.util;

import org.springframework.http.ResponseEntity;
import com.rocketFoodDelivery.rocketFood.dtos.ApiResponseDto;

import org.springframework.http.HttpStatus;

/**
 * Custom utility class for handling API responses. Only manages success responses. Error responses
 * are managed by the {@link com.rocketFoodDelivery.rocketFood.controller.GlobalExceptionHandler} class
 */

public class ResponseBuilder {

    public static ResponseEntity<Object> buildOkResponse(Object data) {
        ApiResponseDto response = new ApiResponseDto();
        response.setMessage("Success");
        response.setData(data);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    public static ResponseEntity<Object> buildCreatedResponse(Object data) {
        ApiResponseDto response = new ApiResponseDto();
        response.setMessage("Success");
        response.setData(data);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}