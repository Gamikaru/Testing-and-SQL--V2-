package com.rocketFoodDelivery.rocketFood.dtos;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ApiOrderRequestDto {
    private int customer_id;
    private int restaurant_id;
    private List<ApiProductForOrderApiDto> products;
}
