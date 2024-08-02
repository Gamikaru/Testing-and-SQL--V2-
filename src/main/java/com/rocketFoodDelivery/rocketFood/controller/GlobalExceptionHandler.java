package com.rocketFoodDelivery.rocketFood.controller;

import com.rocketFoodDelivery.rocketFood.dtos.ApiErrorDto;
import com.rocketFoodDelivery.rocketFood.dtos.ApiResponseStatusDto;
import com.rocketFoodDelivery.rocketFood.dtos.AuthResponseErrorDto;
import com.rocketFoodDelivery.rocketFood.exception.*;
import com.rocketFoodDelivery.rocketFood.util.ResponseBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

        @ExceptionHandler(ValidationException.class)
        public ResponseEntity<ApiErrorDto> handleValidationException(ValidationException ex) {
                log.error("Validation error: {}", ex.getMessage(), ex);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .body(ApiErrorDto.builder().error("Validation failed").details(ex.getMessage())
                                                .build());
        }

        @ExceptionHandler(ResourceNotFoundException.class)
        public ResponseEntity<ApiErrorDto> handleResourceNotFoundException(ResourceNotFoundException ex) {
                log.error("Resource not found: {}", ex.getMessage(), ex);
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                .body(ApiErrorDto.builder().error("Resource not found").details(ex.getMessage())
                                                .build());
        }

        @ExceptionHandler(BadRequestException.class)
        public ResponseEntity<ApiErrorDto> handleBadRequestException(BadRequestException ex) {
                log.error("Bad request: {}", ex.getMessage(), ex);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .body(ApiErrorDto.builder().error("Invalid or missing parameters")
                                                .details(ex.getMessage()).build());
        }

        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<ApiResponseStatusDto> handleMethodArgumentNotValidException(
                        MethodArgumentNotValidException ex) {
                log.error("Validation error: {}", ex.getMessage(), ex);

                Map<String, String> errors = new HashMap<>();
                ex.getBindingResult().getFieldErrors()
                                .forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));

                String message = errors.values().stream().findFirst().orElse("Invalid or missing parameters");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .body(ApiResponseStatusDto.builder().success(false).message(message).build());
        }

        @ExceptionHandler({ BadCredentialsException.class, UsernameNotFoundException.class })
        public ResponseEntity<AuthResponseErrorDto> handleAuthenticationException(Exception ex) {
                log.error("Authentication error: {}", ex.getMessage(), ex);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                .body(AuthResponseErrorDto.builder().success(false)
                                                .message("Authentication failed. Please check your credentials.")
                                                .build());
        }

        @ExceptionHandler(IllegalArgumentException.class)
        public ResponseEntity<ApiErrorDto> handleIllegalArgumentException(IllegalArgumentException ex) {
                log.error("Illegal argument: {}", ex.getMessage(), ex);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .body(ApiErrorDto.builder().error("Invalid argument").details(ex.getMessage()).build());
        }

        @ExceptionHandler(Exception.class)
        public ResponseEntity<ApiErrorDto> handleGenericException(Exception ex) {
                log.error("Unexpected error: {}", ex.getMessage(), ex);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body(ApiErrorDto.builder().error("Internal server error").details(ex.getMessage())
                                                .build());
        }
}
