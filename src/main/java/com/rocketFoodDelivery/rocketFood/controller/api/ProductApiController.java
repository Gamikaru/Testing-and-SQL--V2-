package com.rocketFoodDelivery.rocketFood.controller.api;

import com.rocketFoodDelivery.rocketFood.dtos.ApiProductDto;
import com.rocketFoodDelivery.rocketFood.exception.ResourceNotFoundException;
import com.rocketFoodDelivery.rocketFood.service.ProductService;
import com.rocketFoodDelivery.rocketFood.util.ResponseBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;

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
            // Changed to use HttpStatus instead of integer
            return ResponseBuilder.buildResponse("Success", products, HttpStatus.OK);
        } catch (Exception ex) {
            log.error("Exception occurred while fetching products: {}", ex.getMessage());
            // Changed to use HttpStatus instead of integer
            return ResponseBuilder.buildErrorResponse("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
