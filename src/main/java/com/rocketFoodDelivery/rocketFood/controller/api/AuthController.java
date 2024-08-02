package com.rocketFoodDelivery.rocketFood.controller.api;

import com.rocketFoodDelivery.rocketFood.service.AuthService;
import com.rocketFoodDelivery.rocketFood.dtos.AuthRequestDto;
import com.rocketFoodDelivery.rocketFood.util.ResponseBuilder;
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

    /**
     * Authenticates the user and generates a JWT token if successful.
     * 
     * @param request The authentication request containing email and password.
     * @return ResponseEntity containing the authentication result.
     */
    @PostMapping
    public ResponseEntity<?> authenticate(@RequestBody @Valid AuthRequestDto request) {
        log.info("Attempting authentication for user: {}", request.getEmail());

        try {
            // Attempt to authenticate the user with the provided email and password
            Authentication authentication = authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
            UserEntity user = (UserEntity) authentication.getPrincipal();

            // Generate JWT token if authentication is successful
            String accessToken = jwtUtil.generateAccessToken(user);

            log.info("Authentication successful for user: {}", user.getEmail());
            return ResponseBuilder.buildAuthSuccessResponse(accessToken); // Return 200 OK with the token
        } catch (BadCredentialsException | UsernameNotFoundException e) {
            log.error("Authentication failed for user: {}", request.getEmail());
            return ResponseBuilder.buildAuthErrorResponse("Invalid email or password", HttpStatus.UNAUTHORIZED.value());
        } catch (Exception e) {
            log.error("Unexpected error during authentication for user: {}", request.getEmail(), e);
            return ResponseBuilder.buildAuthErrorResponse("Unexpected error occurred",
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }
}
