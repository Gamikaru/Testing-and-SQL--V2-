package com.rocketFoodDelivery.rocketFood.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
//this is for the get order api.
public class ApiOrderRequestDto {
    int id;
    String type;
}
