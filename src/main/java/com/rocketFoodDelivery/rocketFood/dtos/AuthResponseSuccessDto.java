package com.rocketFoodDelivery.rocketFood.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Data Transfer Object for successful authentication response.
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class AuthResponseSuccessDto {
    private boolean success; // Indicates if the authentication was successful
    private String accessToken; // The access token for the authenticated user
}
