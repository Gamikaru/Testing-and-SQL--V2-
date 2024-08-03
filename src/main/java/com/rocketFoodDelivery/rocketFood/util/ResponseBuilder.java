package com.rocketFoodDelivery.rocketFood.util;

import com.rocketFoodDelivery.rocketFood.dtos.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
public class ResponseBuilder {

    public static ResponseEntity<ApiResponseDto> buildBadRequestResponse(String message) {
        return buildResponse(message, null, HttpStatus.BAD_REQUEST);
    }

    public static ResponseEntity<ApiResponseDto> buildNotFoundResponse(String message) {
        return buildResponse(message, null, HttpStatus.NOT_FOUND);
    }

    public static ResponseEntity<ApiResponseDto> buildErrorResponse(String message, HttpStatus status) {
        return buildResponse(message, null, status);
    }

    public static ResponseEntity<ApiResponseDto> buildResponse(String message, Object data, HttpStatus status) {
        ApiResponseDto response = ApiResponseDto.builder()
                .message(message)
                .data(data)
                .build();
        log.info("Building response with status {}: {}", status, response);
        return ResponseEntity.status(status).body(response);
    }

    public static ResponseEntity<AuthResponseSuccessDto> buildAuthSuccessResponse(String accessToken) {
        AuthResponseSuccessDto response = AuthResponseSuccessDto.builder()
                .success(true)
                .accessToken(accessToken)
                .build();
        log.info("Building AuthSuccess response: {}", response);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    public static ResponseEntity<AuthResponseErrorDto> buildAuthErrorResponse(String message, HttpStatus status) {
        AuthResponseErrorDto response = AuthResponseErrorDto.builder()
                .success(false)
                .message(message)
                .build();
        log.info("Building AuthError response: {}", response);
        return ResponseEntity.status(status).body(response);
    }

    /**
     * Builds an error response with the given message and HTTP status.
     *
     * @param response the HttpServletResponse.
     * @param message  the error message.
     * @param status   the HTTP status.
     * @throws IOException if an input or output exception occurs.
     */
    public static void buildErrorResponse(HttpServletResponse response, String message, HttpStatus status)
            throws IOException {
        ApiResponseDto apiResponse = ApiResponseDto.builder()
                .message(message)
                .data(null)
                .build();
        log.info("Building error response with status {}: {}", status.value(), apiResponse);
        response.setStatus(status.value());
        response.setContentType("application/json");
        try {
            response.getWriter().write(apiResponse.toJson());
        } catch (JsonProcessingException e) {
            log.error("Error converting ApiResponseDto to JSON", e);
            response.getWriter().write("{\"message\":\"" + message + "\", \"data\":null}");
        }
    }
}
