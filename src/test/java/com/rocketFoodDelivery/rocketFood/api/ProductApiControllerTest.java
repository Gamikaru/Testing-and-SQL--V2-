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

/**
 * Integration tests for the ProductApiController.
 */
@SpringBootTest(classes = RocketFoodApplication.class)
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
public class ProductApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String BASE_URI = "/api/products";
    private static final Integer VALID_RESTAURANT_ID = 1;
    private static final Integer INVALID_RESTAURANT_ID = -1;
    private static final String SUCCESS_MESSAGE = "Success";
    private static final String RESOURCE_NOT_FOUND_MESSAGE = "No products found for restaurant ID ";
    private static final String INTERNAL_SERVER_ERROR_MESSAGE = "Internal server error";

    private ApiProductDto productDto;

    /**
     * Setup method to initialize test data before each test.
     */
    @BeforeEach
    public void setUp() {
        productDto = ApiProductDto.builder().id(1).name("Cheeseburger").cost(525).build();
    }

    /**
     * Tests fetching products with a valid restaurant ID.
     * 
     * @throws Exception if an error occurs during the test.
     */
    @Test
    @WithMockUser
    public void testGetProducts() throws Exception {
        List<ApiProductDto> products = Collections.singletonList(productDto);
        Mockito.when(productService.getProductsByRestaurantId(VALID_RESTAURANT_ID)).thenReturn(products);

        performGetRequest(VALID_RESTAURANT_ID, 200, SUCCESS_MESSAGE, products);
    }

    /**
     * Tests fetching products when no products are found for a valid restaurant ID.
     * 
     * @throws Exception if an error occurs during the test.
     */
    @Test
    @WithMockUser
    public void testGetProducts_NoProductsFound() throws Exception {
        Mockito.when(productService.getProductsByRestaurantId(VALID_RESTAURANT_ID)).thenReturn(Collections.emptyList());

        performGetRequest(VALID_RESTAURANT_ID, 404, RESOURCE_NOT_FOUND_MESSAGE + VALID_RESTAURANT_ID, null);
    }

    /**
     * Tests fetching products when an internal server error occurs.
     * 
     * @throws Exception if an error occurs during the test.
     */
    @Test
    @WithMockUser
    public void testGetProducts_InternalServerError() throws Exception {
        Mockito.when(productService.getProductsByRestaurantId(VALID_RESTAURANT_ID))
                .thenThrow(new RuntimeException(INTERNAL_SERVER_ERROR_MESSAGE));

        performGetRequest(VALID_RESTAURANT_ID, 500, INTERNAL_SERVER_ERROR_MESSAGE, null);
    }

    /**
     * Tests fetching products with an invalid restaurant ID.
     * 
     * @throws Exception if an error occurs during the test.
     */
    @Test
    @WithMockUser
    public void testGetProducts_InvalidRestaurantId() throws Exception {
        Mockito.when(productService.getProductsByRestaurantId(INVALID_RESTAURANT_ID))
                .thenReturn(Collections.emptyList());

        performGetRequest(INVALID_RESTAURANT_ID, 404, RESOURCE_NOT_FOUND_MESSAGE + INVALID_RESTAURANT_ID, null);
    }

    /**
     * Helper method to perform GET request and validate the response.
     * 
     * @param restaurantId   the restaurant ID to fetch products for.
     * @param expectedStatus the expected HTTP status code.
     * @param expectedMessage the expected response message.
     * @param expectedData the expected data in the response.
     * @throws Exception if an error occurs during the test.
     */
    private void performGetRequest(Integer restaurantId, int expectedStatus, String expectedMessage,
            List<ApiProductDto> expectedData) throws Exception {
        var resultActions = mockMvc.perform(get(BASE_URI)
                .param("restaurant", restaurantId.toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(expectedStatus))
                .andExpect(jsonPath("$.message").value(expectedMessage))
                .andExpect(jsonPath("$.data", expectedData == null ? nullValue() : hasSize(expectedData.size())));

        if (expectedData != null) {
            for (int i = 0; i < expectedData.size(); i++) {
                ApiProductDto expectedProduct = expectedData.get(i);
                resultActions.andExpect(jsonPath("$.data[" + i + "].name").value(expectedProduct.getName()))
                        .andExpect(jsonPath("$.data[" + i + "].cost").value(expectedProduct.getCost()));
            }
        }
    }
}
