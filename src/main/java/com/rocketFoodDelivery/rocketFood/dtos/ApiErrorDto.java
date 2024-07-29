package com.rocketFoodDelivery.rocketFood.dtos;

import lombok.Getter;
import lombok.Setter;
import lombok.Builder;


@Getter
@Setter
@Builder


/* Used to return API errors. */
public class ApiErrorDto {
    String error;
    String details;
}
