package com.rocketFoodDelivery.rocketFood.dtos;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
//this is a class created to process the order details for the order api from postman.
public class ApiOrderDto {
    int id ;
    int customer_id;
    String customer_name;
    String customer_address;
    int restaurant_id;
    String restaurant_name;
    String restaurant_address;
    String status;
    List <ApiProductForOrderApiDto> products;
    long total_cost;
}
