package com.rocketFoodDelivery.rocketFood.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rocketFoodDelivery.rocketFood.RocketFoodApplication;
import com.rocketFoodDelivery.rocketFood.dtos.ApiAddressDto;
import com.rocketFoodDelivery.rocketFood.dtos.ApiCreateRestaurantDto;
import com.rocketFoodDelivery.rocketFood.dtos.ApiRestaurantDto;
import com.rocketFoodDelivery.rocketFood.exception.ResourceNotFoundException;
import com.rocketFoodDelivery.rocketFood.models.Address;
import com.rocketFoodDelivery.rocketFood.models.UserEntity;
import com.rocketFoodDelivery.rocketFood.service.RestaurantService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;


import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the RestaurantApiController.
 */
@SpringBootTest(classes = RocketFoodApplication.class)
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
public class RestaurantApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RestaurantService restaurantService;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String BASE_URI = "/api/restaurants";
    private static final String VALID_PHONE = "123-456-7890";
    private static final String INVALID_PHONE = "invalid-phone";
    private static final String VALID_EMAIL = "test@restaurant.com";
    private static final String INVALID_EMAIL = "invalid-email";
    private static final String VALID_NAME = "Test Restaurant";
    private static final String UPDATED_NAME = "Updated Restaurant";
    private static final String STREET_ADDRESS = "123 Main St";
    private static final String CITY = "Springfield";
    private static final String POSTAL_CODE = "12345";
    private static final String SUCCESS_MESSAGE = "Success";
    private static final String NOT_FOUND_MESSAGE = "Restaurant with id 999 not found";
    private static final String USER_ID_REQUIRED_MESSAGE = "User ID is required";
    private static final String ADDRESS_REQUIRED_MESSAGE = "Address is required";
    private static final String INTERNAL_SERVER_ERROR_MESSAGE = "Internal server error";

    private ApiCreateRestaurantDto validRestaurantDto;
    private Address validAddress;
    private UserEntity validUser;

    /**
     * Setup method to initialize test data before each test.
     */
    @BeforeEach
    public void setUp() {
        validAddress = createValidAddress();
        validUser = createValidUser();
        validRestaurantDto = createValidRestaurantDto();
    }

    private Address createValidAddress() {
        return Address.builder()
                .streetAddress(STREET_ADDRESS)
                .city(CITY)
                .postalCode(POSTAL_CODE)
                .build();
    }

    private UserEntity createValidUser() {
        return UserEntity.builder()
                .name("Test User")
                .email("test@user.com")
                .password("password")
                .build();
    }

    private ApiCreateRestaurantDto createValidRestaurantDto() {
        return ApiCreateRestaurantDto.builder()
                .name(VALID_NAME)
                .phone(VALID_PHONE)
                .email(VALID_EMAIL)
                .priceRange(2)
                .userId(validUser.getId())
                .address(ApiAddressDto.builder()
                        .streetAddress(validAddress.getStreetAddress())
                        .city(validAddress.getCity())
                        .postalCode(validAddress.getPostalCode())
                        .build())
                .build();
    }

    @AfterEach
    public void tearDown() {
    }

    /**
     * Tests creating a restaurant with valid data.
     * 
     * @throws Exception if an error occurs during the test.
     */
    @Test
    @WithMockUser
    public void testCreateRestaurantWithValidData() throws Exception {
        ApiRestaurantDto createdRestaurant = ApiRestaurantDto.builder()
                .id(1)
                .name(validRestaurantDto.getName())
                .priceRange(validRestaurantDto.getPriceRange())
                .rating(5)
                .build();

        Mockito.when(restaurantService.createRestaurant(Mockito.any(ApiCreateRestaurantDto.class)))
                .thenReturn(createdRestaurant);

        performPostRequest(validRestaurantDto, 201, SUCCESS_MESSAGE, createdRestaurant);
    }

    /**
     * Tests creating a restaurant with missing fields.
     * 
     * @throws Exception if an error occurs during the test.
     */
    @Test
    @WithMockUser
    public void testCreateRestaurantWithMissingFields() throws Exception {
        ApiCreateRestaurantDto incompleteRestaurantDto = ApiCreateRestaurantDto.builder()
                .name(VALID_NAME)
                .phone(VALID_PHONE)
                .build();

        performPostRequest(incompleteRestaurantDto, 400, ADDRESS_REQUIRED_MESSAGE, null);
    }

    /**
     * Tests creating a restaurant with invalid data.
     * 
     * @throws Exception if an error occurs during the test.
     */
    @Test
    @WithMockUser
    public void testCreateRestaurantWithInvalidData() throws Exception {
        ApiCreateRestaurantDto invalidRestaurantDto = ApiCreateRestaurantDto.builder()
                .name("")
                .phone(INVALID_PHONE)
                .email(INVALID_EMAIL)
                .priceRange(2)
                .address(validRestaurantDto.getAddress())
                .build();

        performPostRequest(invalidRestaurantDto, 400, USER_ID_REQUIRED_MESSAGE, null);
    }

    /**
     * Tests fetching restaurants without filters.
     * 
     * @throws Exception if an error occurs during the test.
     */
    @Test
    @WithMockUser
    public void testFetchRestaurantsWithoutFilters() throws Exception {
        ApiRestaurantDto restaurantDto = ApiRestaurantDto.builder()
                .id(1)
                .name(validRestaurantDto.getName())
                .priceRange(validRestaurantDto.getPriceRange())
                .rating(5)
                .build();

        Mockito.when(restaurantService.getRestaurants(null, null)).thenReturn(List.of(restaurantDto));

        performGetRequest(null, null, 200, SUCCESS_MESSAGE, List.of(restaurantDto));
    }

    /**
     * Tests fetching restaurants with a rating filter.
     * 
     * @throws Exception if an error occurs during the test.
     */
    @Test
    @WithMockUser
    public void testFetchRestaurantsWithRatingFilter() throws Exception {
        performGetRequest("5", null, 200, SUCCESS_MESSAGE, List.of());
    }

    /**
     * Tests fetching restaurants with a price range filter.
     * 
     * @throws Exception if an error occurs during the test.
     */
    @Test
    @WithMockUser
    public void testFetchRestaurantsWithPriceRangeFilter() throws Exception {
        ApiRestaurantDto restaurantDto = ApiRestaurantDto.builder()
                .id(1)
                .name(validRestaurantDto.getName())
                .priceRange(validRestaurantDto.getPriceRange())
                .rating(5)
                .build();

        Mockito.when(restaurantService.getRestaurants(null, 2)).thenReturn(List.of(restaurantDto));

        performGetRequest(null, "2", 200, SUCCESS_MESSAGE, List.of(restaurantDto));
    }

    /**
     * Tests fetching restaurants with rating and price range filters.
     * 
     * @throws Exception if an error occurs during the test.
     */
    @Test
    @WithMockUser
    public void testFetchRestaurantsWithRatingAndPriceRangeFilters() throws Exception {
        performGetRequest("5", "2", 200, SUCCESS_MESSAGE, List.of());
    }

    /**
     * Tests fetching a restaurant by a valid ID.
     * 
     * @throws Exception if an error occurs during the test.
     */
    @Test
    @WithMockUser
    public void testFetchRestaurantByValidId() throws Exception {
        ApiRestaurantDto restaurantDto = ApiRestaurantDto.builder()
                .id(1)
                .name(validRestaurantDto.getName())
                .priceRange(validRestaurantDto.getPriceRange())
                .rating(5)
                .build();

        Mockito.when(restaurantService.getRestaurantById(1)).thenReturn(restaurantDto);

        performGetRequestById(1, 200, SUCCESS_MESSAGE, restaurantDto);
    }

    /**
     * Tests fetching a restaurant by an invalid ID.
     * 
     * @throws Exception if an error occurs during the test.
     */
    @Test
    @WithMockUser
    public void testFetchRestaurantByInvalidId() throws Exception {
        Mockito.when(restaurantService.getRestaurantById(999))
                .thenThrow(new ResourceNotFoundException(NOT_FOUND_MESSAGE));

        performGetRequestById(999, 404, NOT_FOUND_MESSAGE, null);
    }

    /**
     * Tests updating a restaurant with valid data.
     * 
     * @throws Exception if an error occurs during the test.
     */
    @Test
    @WithMockUser
    public void testUpdateRestaurantWithValidData() throws Exception {
        ApiCreateRestaurantDto updatedRestaurantDto = ApiCreateRestaurantDto.builder()
                .name(UPDATED_NAME)
                .phone("098-765-4321")
                .email("updated@restaurant.com")
                .priceRange(3)
                .address(ApiAddressDto.builder()
                        .streetAddress(STREET_ADDRESS)
                        .city(CITY)
                        .postalCode(POSTAL_CODE)
                        .build())
                .userId(1)
                .build();

        ApiRestaurantDto updatedRestaurant = ApiRestaurantDto.builder()
                .id(1)
                .name(updatedRestaurantDto.getName())
                .priceRange(updatedRestaurantDto.getPriceRange())
                .rating(5)
                .build();

        Mockito.when(restaurantService.updateRestaurant(Mockito.eq(1), Mockito.any(ApiCreateRestaurantDto.class)))
                .thenReturn(updatedRestaurant);

        performPutRequest(1, updatedRestaurantDto, 200, SUCCESS_MESSAGE, updatedRestaurant);
    }

    /**
     * Tests updating a restaurant with missing fields.
     * 
     * @throws Exception if an error occurs during the test.
     */
    @Test
    @WithMockUser
    public void testUpdateRestaurantWithMissingFields() throws Exception {
        ApiCreateRestaurantDto incompleteRestaurantDto = ApiCreateRestaurantDto.builder()
                .name(UPDATED_NAME)
                .build();

        performPutRequest(1, incompleteRestaurantDto, 400, ADDRESS_REQUIRED_MESSAGE, null);
    }

    /**
     * Tests updating a restaurant with invalid data.
     * 
     * @throws Exception if an error occurs during the test.
     */
    @Test
    @WithMockUser
    public void testUpdateRestaurantWithInvalidData() throws Exception {
        ApiCreateRestaurantDto invalidRestaurantDto = ApiCreateRestaurantDto.builder()
                .name("")
                .phone(INVALID_PHONE)
                .email(INVALID_EMAIL)
                .priceRange(2)
                .address(validRestaurantDto.getAddress())
                .build();

        performPutRequest(1, invalidRestaurantDto, 400, USER_ID_REQUIRED_MESSAGE, null);
    }

    /**
     * Tests updating a restaurant with a non-existing ID.
     * 
     * @throws Exception if an error occurs during the test.
     */
    @Test
    @WithMockUser
    public void testUpdateRestaurantWithNonExistingId() throws Exception {
        ApiCreateRestaurantDto updatedRestaurantDto = ApiCreateRestaurantDto.builder()
                .name(UPDATED_NAME)
                .phone("098-765-4321")
                .email("updated@restaurant.com")
                .priceRange(3)
                .address(ApiAddressDto.builder()
                        .streetAddress(STREET_ADDRESS)
                        .city(CITY)
                        .postalCode(POSTAL_CODE)
                        .build())
                .userId(1)
                .build();

        Mockito.when(restaurantService.updateRestaurant(Mockito.eq(999), Mockito.any(ApiCreateRestaurantDto.class)))
                .thenThrow(new ResourceNotFoundException(NOT_FOUND_MESSAGE));

        performPutRequest(999, updatedRestaurantDto, 404, NOT_FOUND_MESSAGE, null);
    }

    /**
     * Tests deleting a restaurant with a valid ID.
     * 
     * @throws Exception if an error occurs during the test.
     */
    @Test
    @WithMockUser
    public void testDeleteRestaurantWithValidId() throws Exception {
        ApiRestaurantDto deletedRestaurant = ApiRestaurantDto.builder()
                .id(1)
                .name(VALID_NAME)
                .priceRange(2)
                .rating(5)
                .build();

        Mockito.when(restaurantService.deleteRestaurant(1)).thenReturn(deletedRestaurant);

        performDeleteRequest(1, 200, SUCCESS_MESSAGE, deletedRestaurant);
    }

    /**
     * Tests deleting a restaurant with an invalid ID.
     * 
     * @throws Exception if an error occurs during the test.
     */
    @Test
    @WithMockUser
    public void testDeleteRestaurantWithInvalidId() throws Exception {
        Mockito.doThrow(new ResourceNotFoundException(NOT_FOUND_MESSAGE))
                .when(restaurantService).deleteRestaurant(999);

        performDeleteRequest(999, 404, NOT_FOUND_MESSAGE, null);
    }

    /**
     * Helper method to perform POST request and validate the response.
     * 
     * @param restaurantDto   the restaurant DTO for the request body.
     * @param expectedStatus  the expected HTTP status code.
     * @param expectedMessage the expected response message.
     * @param expectedData    the expected data in the response.
     * @throws Exception if an error occurs during the test.
     */
    private void performPostRequest(ApiCreateRestaurantDto restaurantDto, int expectedStatus, String expectedMessage,
            ApiRestaurantDto expectedData) throws Exception {
        var resultActions = mockMvc.perform(post(BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(restaurantDto)))
                .andExpect(status().is(expectedStatus))
                .andExpect(jsonPath("$.message").value(expectedMessage));

        if (expectedData != null) {
            resultActions.andExpect(jsonPath("$.data.name").value(expectedData.getName()))
                    .andExpect(jsonPath("$.data.price_range").value(expectedData.getPriceRange()))
                    .andExpect(jsonPath("$.data.rating").value(expectedData.getRating()));
        } else {
            resultActions.andExpect(jsonPath("$.data").doesNotExist());
        }
    }

    /**
     * Helper method to perform GET request and validate the response.
     * 
     * @param rating          the rating filter parameter.
     * @param priceRange      the price range filter parameter.
     * @param expectedStatus  the expected HTTP status code.
     * @param expectedMessage the expected response message.
     * @param expectedData    the expected data in the response.
     * @throws Exception if an error occurs during the test.
     */
    private void performGetRequest(String rating, String priceRange, int expectedStatus, String expectedMessage,
            List<ApiRestaurantDto> expectedData) throws Exception {
        var requestBuilder = get(BASE_URI).contentType(MediaType.APPLICATION_JSON);

        if (rating != null) {
            requestBuilder.param("rating", rating);
        }

        if (priceRange != null) {
            requestBuilder.param("price_range", priceRange);
        }

        var resultActions = mockMvc.perform(requestBuilder)
                .andExpect(status().is(expectedStatus))
                .andExpect(jsonPath("$.message").value(expectedMessage));

        if (expectedData != null) {
            resultActions.andExpect(jsonPath("$.data", hasSize(expectedData.size())));
            for (int i = 0; i < expectedData.size(); i++) {
                resultActions.andExpect(jsonPath("$.data[" + i + "].name").value(expectedData.get(i).getName()))
                        .andExpect(jsonPath("$.data[" + i + "].price_range").value(expectedData.get(i).getPriceRange()))
                        .andExpect(jsonPath("$.data[" + i + "].rating").value(expectedData.get(i).getRating()));
            }
        } else {
            resultActions.andExpect(jsonPath("$.data").doesNotExist());
        }
    }

    /**
     * Helper method to perform GET request by ID and validate the response.
     * 
     * @param id              the restaurant ID.
     * @param expectedStatus  the expected HTTP status code.
     * @param expectedMessage the expected response message.
     * @param expectedData    the expected data in the response.
     * @throws Exception if an error occurs during the test.
     */
    private void performGetRequestById(int id, int expectedStatus, String expectedMessage,
            ApiRestaurantDto expectedData) throws Exception {
        var resultActions = mockMvc.perform(get(BASE_URI + "/{id}", id)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(expectedStatus))
                .andExpect(jsonPath("$.message").value(expectedMessage));

        if (expectedData != null) {
            resultActions.andExpect(jsonPath("$.data.name").value(expectedData.getName()))
                    .andExpect(jsonPath("$.data.price_range").value(expectedData.getPriceRange()))
                    .andExpect(jsonPath("$.data.rating").value(expectedData.getRating()));
        } else {
            resultActions.andExpect(jsonPath("$.data").doesNotExist());
        }
    }

    /**
     * Helper method to perform PUT request and validate the response.
     * 
     * @param id              the restaurant ID.
     * @param restaurantDto   the restaurant DTO for the request body.
     * @param expectedStatus  the expected HTTP status code.
     * @param expectedMessage the expected response message.
     * @param expectedData    the expected data in the response.
     * @throws Exception if an error occurs during the test.
     */
    private void performPutRequest(int id, ApiCreateRestaurantDto restaurantDto, int expectedStatus,
            String expectedMessage, ApiRestaurantDto expectedData) throws Exception {
        var resultActions = mockMvc.perform(put(BASE_URI + "/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(restaurantDto)))
                .andExpect(status().is(expectedStatus))
                .andExpect(jsonPath("$.message").value(expectedMessage));

        if (expectedData != null) {
            resultActions.andExpect(jsonPath("$.data.name").value(expectedData.getName()))
                    .andExpect(jsonPath("$.data.price_range").value(expectedData.getPriceRange()))
                    .andExpect(jsonPath("$.data.rating").value(expectedData.getRating()));
        } else {
            resultActions.andExpect(jsonPath("$.data").doesNotExist());
        }
    }

    /**
     * Helper method to perform DELETE request and validate the response.
     * 
     * @param id              the restaurant ID.
     * @param expectedStatus  the expected HTTP status code.
     * @param expectedMessage the expected response message.
     * @param expectedData    the expected data in the response.
     * @throws Exception if an error occurs during the test.
     */
    private void performDeleteRequest(int id, int expectedStatus, String expectedMessage, ApiRestaurantDto expectedData)
            throws Exception {
        var resultActions = mockMvc.perform(delete(BASE_URI + "/{id}", id)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(expectedStatus))
                .andExpect(jsonPath("$.message").value(expectedMessage));

        if (expectedData != null) {
            resultActions.andExpect(jsonPath("$.data.id").value(expectedData.getId()))
                    .andExpect(jsonPath("$.data.name").value(expectedData.getName()))
                    .andExpect(jsonPath("$.data.price_range").value(expectedData.getPriceRange()))
                    .andExpect(jsonPath("$.data.rating").value(expectedData.getRating()));
        } else {
            resultActions.andExpect(jsonPath("$.data").doesNotExist());
        }
    }
    
}
