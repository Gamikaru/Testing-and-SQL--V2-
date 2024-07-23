package com.rocketFoodDelivery.rocketFood.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.rocketFoodDelivery.rocketFood.dtos.ApiErrorDto;
import com.rocketFoodDelivery.rocketFood.exception.*;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ApiErrorDto> handleValidationException(ValidationException ex) {
        ApiErrorDto response = new ApiErrorDto();
        response.setError("Validation failed");
        response.setDetails(ex.getErrors().getAllErrors().toString());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ApiErrorDto> handleResourceNotFoundException(ResourceNotFoundException ex) {
        ApiErrorDto response = new ApiErrorDto();
        response.setError("Resource not found");
        response.setDetails(ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ApiErrorDto> handleBadRequestException(BadRequestException ex) {
        ApiErrorDto response = new ApiErrorDto();
        response.setError("Invalid or missing parameters");
        response.setDetails(ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}