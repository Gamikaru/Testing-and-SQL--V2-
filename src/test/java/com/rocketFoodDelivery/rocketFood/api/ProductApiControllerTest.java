package com.rocketFoodDelivery.rocketFood.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rocketFoodDelivery.rocketFood.RocketFoodApplication;
import com.rocketFoodDelivery.rocketFood.dtos.ApiProductDto;
import com.rocketFoodDelivery.rocketFood.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// Define the Spring Boot test class for ProductApiController
@SpringBootTest(classes = RocketFoodApplication.class)
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
public class ProductApiControllerTest {

    @Autowired
    private MockMvc mockMvc; // MockMvc to perform HTTP requests in tests

    @MockBean
    private ProductService productService; // Mock ProductService bean

    @Autowired
    private ObjectMapper objectMapper; // ObjectMapper for JSON serialization/deserialization

    // Define constants for reuse
    private static final String BASE_URI = "/api/products";
    private static final Integer VALID_RESTAURANT_ID = 1;
    private static final Integer INVALID_RESTAURANT_ID = -1;
    private static final String SUCCESS_MESSAGE = "Success";
    private static final String RESOURCE_NOT_FOUND_MESSAGE = "Resource not found";
    private static final String INTERNAL_SERVER_ERROR_MESSAGE = "Internal server error";

    private ApiProductDto productDto; // DTO for product

    // Initialize test data before each test
    @BeforeEach
    public void setUp() {
        productDto = ApiProductDto.builder().id(1).name("Cheeseburger").cost(525).build();
    }

    // Test fetching products successfully
    @Test
    @WithMockUser
    public void testGetProducts() throws Exception {
        // Setup mock products list
        List<ApiProductDto> products = Collections.singletonList(productDto);
        // Mock the behavior of productService
        Mockito.when(productService.getProductsByRestaurantId(VALID_RESTAURANT_ID)).thenReturn(products);

        // Perform GET request to fetch products and verify the response
        performGetRequest(VALID_RESTAURANT_ID, 200, SUCCESS_MESSAGE, products);
    }

    // Test fetching products when no products are found
    @Test
    @WithMockUser
    public void testGetProducts_NoProductsFound() throws Exception {
        // Mock the behavior of productService to return empty list
        Mockito.when(productService.getProductsByRestaurantId(VALID_RESTAURANT_ID)).thenReturn(Collections.emptyList());

        // Perform GET request to fetch products and verify the response
        performGetRequest(VALID_RESTAURANT_ID, 404, RESOURCE_NOT_FOUND_MESSAGE, null);
    }

    // Test fetching products when an internal server error occurs
    @Test
    @WithMockUser
    public void testGetProducts_InternalServerError() throws Exception {
        // Mock the behavior of productService to throw exception
        Mockito.when(productService.getProductsByRestaurantId(VALID_RESTAURANT_ID))
                .thenThrow(new RuntimeException(INTERNAL_SERVER_ERROR_MESSAGE));

        // Perform GET request to fetch products and verify the response
        performGetRequest(VALID_RESTAURANT_ID, 500, INTERNAL_SERVER_ERROR_MESSAGE, null);
    }

    // Test fetching products with an invalid restaurant ID
    @Test
    @WithMockUser
    public void testGetProducts_InvalidRestaurantId() throws Exception {
        // Mock the behavior of productService to return empty list
        Mockito.when(productService.getProductsByRestaurantId(INVALID_RESTAURANT_ID))
                .thenReturn(Collections.emptyList());

        // Perform GET request to fetch products and verify the response
        performGetRequest(INVALID_RESTAURANT_ID, 404, RESOURCE_NOT_FOUND_MESSAGE, null);
    }

    // Helper method to perform GET request and verify the response
    private void performGetRequest(Integer restaurantId, int expectedStatus, String expectedMessage,
            List<ApiProductDto> expectedData) throws Exception {
        // Perform GET request
        mockMvc.perform(get(BASE_URI)
                .param("restaurant", restaurantId.toString())
                .contentType(MediaType.APPLICATION_JSON))
                // Verify status and message
                .andExpect(status().is(expectedStatus))
                .andExpect(jsonPath("$.message").value(expectedMessage))
                // Verify data if not null, else expect null
                .andExpect(jsonPath("$.data", expectedData == null ? nullValue() : hasSize(expectedData.size())));

        // If expectedData is not null, verify each product
        if (expectedData != null) {
            for (int i = 0; i < expectedData.size(); i++) {
                ApiProductDto expectedProduct = expectedData.get(i);
                mockMvc.perform(get(BASE_URI)
                        .param("restaurant", restaurantId.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                        // Verify product details
                        .andExpect(jsonPath("$.data[" + i + "].name", is(expectedProduct.getName())))
                        .andExpect(jsonPath("$.data[" + i + "].cost", is(expectedProduct.getCost())));
            }
        }
    }
}
