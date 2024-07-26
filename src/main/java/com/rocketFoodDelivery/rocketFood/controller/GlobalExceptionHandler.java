package com.rocketFoodDelivery.rocketFood.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.rocketFoodDelivery.rocketFood.dtos.ApiErrorDto;
import com.rocketFoodDelivery.rocketFood.exception.*;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ApiErrorDto> handleValidationException(ValidationException ex) {
        logger.error("ValidationException occurred: {}", ex.getErrors().getAllErrors().toString());
        ApiErrorDto response = new ApiErrorDto();
        response.setError("Validation failed");
        response.setDetails(ex.getErrors().getAllErrors().toString());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ApiErrorDto> handleResourceNotFoundException(ResourceNotFoundException ex) {
        logger.error("ResourceNotFoundException occurred: {}", ex.getMessage());
        ApiErrorDto response = new ApiErrorDto();
        response.setError("Resource not found");
        response.setDetails(ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ApiErrorDto> handleBadRequestException(BadRequestException ex) {
        logger.error("BadRequestException occurred: {}", ex.getMessage());
        ApiErrorDto response = new ApiErrorDto();
        response.setError("Invalid or missing parameters");
        response.setDetails(ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}