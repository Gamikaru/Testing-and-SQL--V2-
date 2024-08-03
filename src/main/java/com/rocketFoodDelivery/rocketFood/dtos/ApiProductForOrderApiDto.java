package com.rocketFoodDelivery.rocketFood.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApiProductForOrderApiDto {
    private Integer productId; // Changed 'id' to 'productId'
    private String product_name;
    private int quantity;
    private int unit_cost;
    private int total_cost;

    public ApiProductForOrderApiDto() {
    }

    public ApiProductForOrderApiDto(Integer productId, String product_name, int quantity, int unit_cost, int total_cost) {
        this.productId = productId;
        this.product_name = product_name;
        this.quantity = quantity;
        this.unit_cost = unit_cost;
        this.total_cost = total_cost;
    }

    // Correct getter method for product ID
    public Integer getProductId() {
        return productId;
    }
}
