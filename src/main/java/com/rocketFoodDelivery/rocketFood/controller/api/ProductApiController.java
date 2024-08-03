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

/**
 * Controller for managing product-related operations.
 */
@Slf4j
@RestController
@RequestMapping("/api/products")
public class ProductApiController {

    private final ProductService productService;

    /**
     * Constructs an instance of ProductApiController with the given ProductService.
     *
     * @param productService The ProductService used for managing products.
     */
    public ProductApiController(ProductService productService) {
        this.productService = productService;
    }

    /**
     * Retrieves a list of products based on the restaurant ID.
     *
     * @param restaurant The ID of the restaurant to fetch products for.
     * @return ResponseEntity with the list of products or an error message.
     */
    @GetMapping
    public ResponseEntity<?> getProducts(@RequestParam Integer restaurant) {
        log.info("Fetching products for restaurant ID: {}", restaurant);

        try {
            List<ApiProductDto> products = productService.getProductsByRestaurantId(restaurant);
            if (products.isEmpty()) {
                // Return a consistent error message
                return ResponseBuilder.buildNotFoundResponse("No products found for restaurant ID " + restaurant);
            }
            log.info("Fetched products: {}", products);
            return ResponseBuilder.buildResponse("Success", products, HttpStatus.OK);
        } catch (Exception ex) {
            log.error("Exception occurred while fetching products: {}", ex.getMessage(), ex);
            return ResponseBuilder.buildErrorResponse("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
