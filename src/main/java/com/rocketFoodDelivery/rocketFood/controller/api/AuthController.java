package com.rocketFoodDelivery.rocketFood.controller.api;

import com.rocketFoodDelivery.rocketFood.service.AuthService;
import com.rocketFoodDelivery.rocketFood.dtos.AuthRequestDto;
import com.rocketFoodDelivery.rocketFood.dtos.AuthResponseSuccessDto;
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

/**
 * Controller for handling authentication-related requests.
 */
@RestController
@RequestMapping("/api/auth")
@Slf4j
public class AuthController {

    private final AuthenticationManager authManager;
    private final JwtUtil jwtUtil;
    private final AuthService authService;

    /**
     * Constructs an instance of AuthController with the given dependencies.
     *
     * @param authManager The AuthenticationManager used for authenticating users.
     * @param jwtUtil     The JwtUtil used for generating JWT tokens.
     * @param authService The AuthService used for authentication logic.
     */
    @Autowired
    public AuthController(AuthenticationManager authManager, JwtUtil jwtUtil, AuthService authService) {
        this.authManager = authManager;
        this.jwtUtil = jwtUtil;
        this.authService = authService;
    }

    /**
     * Authenticates a user and generates a JWT token if the authentication is
     * successful.
     *
     * @param request The authentication request containing the user's email and
     *                password.
     * @return ResponseEntity containing the authentication result, with a 200 OK
     *         status if successful,
     *         or an error response with an appropriate HTTP status code if
     *         authentication fails.
     */
    @PostMapping
    public ResponseEntity<?> authenticate(@RequestBody @Valid AuthRequestDto request) {
        log.info("Attempting authentication for user: {}", request.getEmail());

        try {
            // Authenticate the user with the provided email and password
            Authentication authentication = authenticateUser(request);
            UserEntity user = (UserEntity) authentication.getPrincipal();

            // Generate JWT token
            String accessToken = jwtUtil.generateAccessToken(user);

            log.info("Authentication successful for user: {}", user.getEmail());
            AuthResponseSuccessDto response = createAuthSuccessResponse(accessToken);
            return ResponseEntity.ok(response);
        } catch (BadCredentialsException | UsernameNotFoundException e) {
            log.error("Authentication failed for user: {}", request.getEmail());
            return ResponseBuilder.buildAuthErrorResponse("Invalid email or password", HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            log.error("Unexpected error during authentication for user: {}", request.getEmail(), e);
            return ResponseBuilder.buildAuthErrorResponse("Unexpected error occurred",
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Authenticates the user with the provided email and password.
     *
     * @param request The authentication request containing the user's email and
     *                password.
     * @return Authentication object containing user details if authentication is
     *         successful.
     */
    private Authentication authenticateUser(AuthRequestDto request) {
        return authManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
    }

    /**
     * Creates an AuthResponseSuccessDto with the given JWT access token.
     *
     * @param accessToken The JWT access token generated for the authenticated user.
     * @return An AuthResponseSuccessDto containing the success status and JWT
     *         access token.
     */
    private AuthResponseSuccessDto createAuthSuccessResponse(String accessToken) {
        return AuthResponseSuccessDto.builder()
                .success(true)
                .accessToken(accessToken)
                .build();
    }
}
