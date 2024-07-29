package com.rocketFoodDelivery.rocketFood.dtos;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;


@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class ApiResponseStatusDto {
    private boolean success;
    private String message;
}