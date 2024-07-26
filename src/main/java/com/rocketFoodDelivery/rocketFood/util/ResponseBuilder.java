package com.rocketFoodDelivery.rocketFood.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import com.rocketFoodDelivery.rocketFood.dtos.ApiResponseDto;
import org.springframework.http.HttpStatus;

/**
 * Custom utility class for handling API responses. Only manages success responses. Error responses
 * are managed by the {@link com.rocketFoodDelivery.rocketFood.controller.GlobalExceptionHandler} class
 */
public class ResponseBuilder {

    private static final Logger logger = LoggerFactory.getLogger(ResponseBuilder.class);

    public static ResponseEntity<Object> buildOkResponse(Object data) {
        ApiResponseDto response = new ApiResponseDto();
        response.setMessage("Success");
        response.setData(data);
        logger.info("Building OK response: {}", response);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    public static ResponseEntity<Object> buildCreatedResponse(Object data) {
        ApiResponseDto response = new ApiResponseDto();
        response.setMessage("Success");
        response.setData(data);
        logger.info("Building Created response: {}", response);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
