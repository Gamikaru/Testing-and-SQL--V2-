package com.rocketFoodDelivery.rocketFood.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.rocketFoodDelivery.rocketFood.models.UserEntity;

@Service
public class AuthService {

    private final AuthenticationManager authManager;

    @Autowired
    public AuthService(AuthenticationManager authManager) {
        this.authManager = authManager;
    }

    public UserEntity authenticate(String email, String password) {
        try {
            Authentication authentication = authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, password));
            return (UserEntity) authentication.getPrincipal();
        } catch (BadCredentialsException e) {
            throw new UsernameNotFoundException("Invalid credentials", e);
        } catch (Exception e) {
            throw new RuntimeException("Authentication failed", e);
        }
    }
}
