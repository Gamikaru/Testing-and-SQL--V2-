package com.rocketFoodDelivery.rocketFood.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Data Transfer Object for authentication request.
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class AuthRequestDto {
    @NotBlank(message = "Email is mandatory")
    @Email(message = "Email should be valid")
    private String email; // The user's email for authentication

    @NotBlank(message = "Password is mandatory")
    private String password; // The user's password for authentication
}
