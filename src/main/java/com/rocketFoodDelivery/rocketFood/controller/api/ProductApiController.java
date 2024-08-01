package com.rocketFoodDelivery.rocketFood.controller.api;

import com.rocketFoodDelivery.rocketFood.dtos.ApiProductDto;
import com.rocketFoodDelivery.rocketFood.exception.ResourceNotFoundException;
import com.rocketFoodDelivery.rocketFood.service.ProductService;
import com.rocketFoodDelivery.rocketFood.util.ResponseBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/products")
public class ProductApiController {

    private final ProductService productService;

    public ProductApiController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public ResponseEntity<?> getProducts(@RequestParam Integer restaurant) {
        log.info("Fetching products for restaurant ID: {}", restaurant);

        try {
            List<ApiProductDto> products = productService.getProductsByRestaurantId(restaurant);
            if (products.isEmpty()) {
                return ResponseBuilder.buildNotFoundResponse("Resource not found");
            }
            log.info("Fetched products: {}", products);
            return ResponseBuilder.buildResponse("Success", products, 200);
        } catch (Exception ex) {
            log.error("Exception occurred while fetching products: {}", ex.getMessage());
            return ResponseBuilder.buildErrorResponse("Internal server error", 500);
        }
    }
}
