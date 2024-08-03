package com.rocketFoodDelivery.rocketFood.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApiCreateRestaurantDto {
    private int id;

    @JsonProperty("user_id")
    @NotNull(message = "User ID is required")
    private Integer userId;

    @NotNull(message = "Name is required")
    private String name;

    @JsonProperty("price_range")
    @Min(value = 1, message = "Price range must be at least 1")
    @Max(value = 3, message = "Price range must be at most 3")
    private int priceRange;

    @NotNull(message = "Phone is required")
    private String phone;

    @Email(message = "Email should be valid")
    @NotNull(message = "Email is required")
    private String email;

    @NotNull(message = "Address is required")
    private ApiAddressDto address;
}
