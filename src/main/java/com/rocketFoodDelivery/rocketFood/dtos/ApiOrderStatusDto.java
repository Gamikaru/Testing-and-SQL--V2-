package com.rocketFoodDelivery.rocketFood.dtos;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Builder
@Getter
@Setter
public class ApiOrderStatusDto {
    private String status;

    public ApiOrderStatusDto() {
    }

    public ApiOrderStatusDto(String status) {
        this.status = status;
    }
}
