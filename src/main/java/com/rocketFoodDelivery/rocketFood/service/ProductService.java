package com.rocketFoodDelivery.rocketFood.service;

import com.rocketFoodDelivery.rocketFood.dtos.ApiProductDto;
import com.rocketFoodDelivery.rocketFood.models.Product;
import com.rocketFoodDelivery.rocketFood.repository.ProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<ApiProductDto> getProductsByRestaurantId(Integer restaurantId) {
        log.info("Fetching products for restaurant ID: {}", restaurantId);
        List<Product> products = productRepository.findProductsByRestaurantId(restaurantId);
        return products.stream()
                .map(this::mapToApiProductDto)
                .collect(Collectors.toList());
    }

    private ApiProductDto mapToApiProductDto(Product product) {
        return ApiProductDto.builder()
                .id(product.getId())
                .name(product.getName())
                .cost(product.getCost())
                .build();
    }
}
