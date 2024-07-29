package com.rocketFoodDelivery.rocketFood.controller.api;

import com.rocketFoodDelivery.rocketFood.service.AuthService;
import com.rocketFoodDelivery.rocketFood.dtos.AuthRequestDto;
import com.rocketFoodDelivery.rocketFood.dtos.AuthResponseErrorDto;
import com.rocketFoodDelivery.rocketFood.dtos.AuthResponseSuccessDto;
import com.rocketFoodDelivery.rocketFood.models.UserEntity;
import com.rocketFoodDelivery.rocketFood.security.JwtUtil;
import org.springframework.security.authentication.AuthenticationManager;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import org.springframework.http.HttpStatus;

@RestController
@RequestMapping("/api/auth")
@Slf4j
public class AuthController {

    private final AuthenticationManager authManager;
    private final JwtUtil jwtUtil;
    private final AuthService authService;

    @Autowired
    public AuthController(AuthenticationManager authManager, JwtUtil jwtUtil, AuthService authService) {
        this.authManager = authManager;
        this.jwtUtil = jwtUtil;
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticate(@RequestBody @Valid AuthRequestDto request) {
        log.info("Attempting authentication for user: {}", request.getEmail());

        try {
            Authentication authentication = authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
            UserEntity user = (UserEntity) authentication.getPrincipal();
            String accessToken = jwtUtil.generateAccessToken(user);

            AuthResponseSuccessDto response = buildSuccessResponse(user, accessToken);

            log.info("Authentication successful for user: {}", user.getEmail());
            return ResponseEntity.ok(response);
        } catch (BadCredentialsException | UsernameNotFoundException e) {
            log.error("Authentication failed for user: {}", request.getEmail());
            return buildErrorResponse("Authentication failed. Please check your credentials.", HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            log.error("Unexpected error during authentication for user: {}", request.getEmail(), e);
            return buildErrorResponse("An unexpected error occurred.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private AuthResponseSuccessDto buildSuccessResponse(UserEntity user, String accessToken) {
        return AuthResponseSuccessDto.builder()
                .success(true)
                .accessToken(accessToken)
                .userId(user.getId())
                .customerId(user.getCustomerId() != null ? user.getCustomerId() : 0)
                .courierId(user.getCourierId() != null ? user.getCourierId() : 0)
                .build();
    }

    private ResponseEntity<AuthResponseErrorDto> buildErrorResponse(String message, HttpStatus status) {
        AuthResponseErrorDto errorResponse = AuthResponseErrorDto.builder()
                .success(false)
                .message(message)
                .build();
        return ResponseEntity.status(status).body(errorResponse);
    }
}
