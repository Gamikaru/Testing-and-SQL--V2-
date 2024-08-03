package com.rocketFoodDelivery.rocketFood.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.rocketFoodDelivery.rocketFood.models.UserEntity;

@Service // This annotation indicates that the class is a service component in Spring
public class AuthService {

    // Dependency injection of AuthenticationManager
    private final AuthenticationManager authManager;

    // Constructor for dependency injection
    @Autowired
    public AuthService(AuthenticationManager authManager) {
        this.authManager = authManager;
    }

    /**
     * Authenticates a user with the provided email and password.
     * 
     * @param email    The user's email.
     * @param password The user's password.
     * @return The authenticated UserEntity.
     * @throws UsernameNotFoundException If the credentials are invalid.
     * @throws RuntimeException          If authentication fails for another reason.
     */
    public UserEntity authenticate(String email, String password) {
        try {
            // Attempt to authenticate the user
            Authentication authentication = authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, password));

            // Return the authenticated user details
            return (UserEntity) authentication.getPrincipal();
        } catch (BadCredentialsException e) {
            // Throw exception if credentials are invalid
            throw new UsernameNotFoundException("Invalid credentials", e);
        } catch (Exception e) {
            // Throw a generic exception if any other error occurs
            throw new RuntimeException("Authentication failed", e);
        }
    }
}
