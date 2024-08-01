package com.rocketFoodDelivery.rocketFood.dtos;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ApiOrderDto {
    private Integer id;
    private Integer customer_id;
    private Integer restaurant_id;
    private Integer courier_id;
    private String status;
    private List<ApiProductForOrderApiDto> products;
    private int total_cost;
}
