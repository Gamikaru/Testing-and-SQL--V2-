package com.rocketFoodDelivery.rocketFood.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApiProductOrderRequestDto {
    private int id;
    private int quantity;

    public ApiProductOrderRequestDto() {
    }

    public ApiProductOrderRequestDto(int id, int quantity) {
        this.id = id;
        this.quantity = quantity;
    }
}
