package com.rocketFoodDelivery.rocketFood.controller.api;

import com.rocketFoodDelivery.rocketFood.dtos.AuthRequestDTO;
import com.rocketFoodDelivery.rocketFood.dtos.AuthResponseErrorDto;
import com.rocketFoodDelivery.rocketFood.dtos.AuthResponseSuccessDto;
import com.rocketFoodDelivery.rocketFood.models.Courier;
import com.rocketFoodDelivery.rocketFood.models.Customer;
import com.rocketFoodDelivery.rocketFood.models.UserEntity;
import com.rocketFoodDelivery.rocketFood.repository.CourierRepository;
import com.rocketFoodDelivery.rocketFood.repository.CustomerRepository;
import com.rocketFoodDelivery.rocketFood.security.JwtUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
public class AuthController {

    @Autowired
    private AuthenticationManager authManager;

    @Autowired
    private JwtUtil jwtUtil;

    private final CourierRepository courierRepository;
    private final CustomerRepository customerRepository;

    public AuthController(CourierRepository courierRepository, CustomerRepository customerRepository) {
        this.courierRepository = courierRepository;
        this.customerRepository = customerRepository;
    }

    /**
     * Authenticate the user and return an appropriate response.
     * 
     * @param request The authentication request containing email and password.
     * @return ResponseEntity containing either the success or error DTO.
     */
    @PostMapping("/api/auth")
    public ResponseEntity<?> authenticate(@RequestBody @Valid AuthRequestDTO request) {
        try {
            // Log attempt to authenticate user
            System.out.println("Attempting authentication for user: " + request.getEmail());

            // Authenticate user
            Authentication authentication = authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(), request.getPassword()));

            // Retrieve authenticated user
            UserEntity user = (UserEntity) authentication.getPrincipal();

            // Generate JWT access token
            String accessToken = jwtUtil.generateAccessToken(user);

            // Retrieve user details for response
            Optional<Courier> courier = courierRepository.findByUserEntityId(user.getId());
            Optional<Customer> customer = customerRepository.findByUserEntityId(user.getId());

            // Prepare successful response
            AuthResponseSuccessDto response = new AuthResponseSuccessDto();
            courier.ifPresent(value -> response.setCourier_id(value.getId()));
            customer.ifPresent(value -> response.setCustomer_id(value.getId()));
            response.setSuccess(true);
            response.setAccessToken(accessToken);
            response.setUser_id(user.getId());

            // Log success and return response
            System.out.println("Authentication successful for user: " + user.getUsername());
            return ResponseEntity.ok(response);
        } catch (BadCredentialsException e) {
            // Log failure and prepare error response
            System.out.println("Authentication failed for user: " + request.getEmail());
            AuthResponseErrorDto response = new AuthResponseErrorDto();
            response.setSuccess(false);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }
}
