package com.rocketFoodDelivery.rocketFood.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApiPostAccountDto {
    String account_type;
    String account_email;
    String account_phone;
}
