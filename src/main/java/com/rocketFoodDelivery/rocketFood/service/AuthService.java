package com.rocketFoodDelivery.rocketFood.service;

import com.rocketFoodDelivery.rocketFood.models.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private AuthenticationManager authManager;

    /**
     * Authenticate a user with the provided email and password.
     * 
     * @param email    The user's email.
     * @param password The user's password.
     * @return The authenticated UserEntity.
     */
    public UserEntity authenticate(String email, String password) {
        // Perform authentication
        Authentication authentication = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password));

        // Return authenticated user
        return (UserEntity) authentication.getPrincipal();
    }
}
