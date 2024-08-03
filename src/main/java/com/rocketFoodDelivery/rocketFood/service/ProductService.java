package com.rocketFoodDelivery.rocketFood.service;

import com.rocketFoodDelivery.rocketFood.dtos.ApiProductDto;
import com.rocketFoodDelivery.rocketFood.models.Product;
import com.rocketFoodDelivery.rocketFood.repository.ProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service class for managing products.
 */
@Slf4j
@Service
public class ProductService {

    private final ProductRepository productRepository;

    /**
     * Constructor for dependency injection of the ProductRepository.
     * 
     * @param productRepository the repository for products.
     */
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    /**
     * Retrieves a list of products for a given restaurant ID.
     * 
     * @param restaurantId the ID of the restaurant.
     * @return a list of products as ApiProductDto.
     */
    public List<ApiProductDto> getProductsByRestaurantId(Integer restaurantId) {
        log.info("Fetching products for restaurant ID: {}", restaurantId);
        List<Product> products = productRepository.findProductsByRestaurantId(restaurantId);
        return products.stream()
                .map(this::mapToApiProductDto)
                .collect(Collectors.toList());
    }

    /**
     * Maps a Product entity to an ApiProductDto.
     *
     * @param product the Product entity.
     * @return the ApiProductDto.
     */
    private ApiProductDto mapToApiProductDto(Product product) {
        return ApiProductDto.builder()
                .id(product.getId())
                .name(product.getName())
                .cost(product.getCost())
                .build();
    }
}
