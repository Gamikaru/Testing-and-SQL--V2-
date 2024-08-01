package com.rocketFoodDelivery.rocketFood.util;

import com.rocketFoodDelivery.rocketFood.dtos.ApiResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
public class ResponseBuilder {

    public static ResponseEntity<ApiResponseDto> buildBadRequestResponse(String message) {
        ApiResponseDto response = ApiResponseDto.builder()
                .message(message)
                .data(null)
                .build();
        log.info("Building BadRequest response: {}", response);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    public static ResponseEntity<ApiResponseDto> buildNotFoundResponse(String message) {
        ApiResponseDto response = ApiResponseDto.builder()
                .message(message)
                .data(null)
                .build();
        log.info("Building NotFound response: {}", response);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    public static ResponseEntity<ApiResponseDto> buildErrorResponse(String message, int status) {
        ApiResponseDto response = ApiResponseDto.builder()
                .message(message)
                .data(null)
                .build();
        log.info("Building error response with status {}: {}", status, response);
        return ResponseEntity.status(status).body(response);
    }

    public static ResponseEntity<ApiResponseDto> buildResponse(String message, Object data, int status) {
        ApiResponseDto response = ApiResponseDto.builder()
                .message(message)
                .data(data)
                .build();
        log.info("Building response with status {}: {}", status, response);
        return ResponseEntity.status(status).body(response);
    }

    public static void buildErrorResponse(HttpServletResponse response, String message, int status) throws IOException {
        ApiResponseDto apiResponse = ApiResponseDto.builder()
                .message(message)
                .data(null)
                .build();
        log.info("Building error response with status {}: {}", status, apiResponse);
        response.setStatus(status);
        response.setContentType("application/json");
        response.getWriter().write(apiResponse.toString());
    }
}
