// RESTAURANT API CONTROLLER TEST

package com.rocketFoodDelivery.rocketFood.api;

// Import necessary packages and classes
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
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import java.util.List;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = RocketFoodApplication.class)
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
public class RestaurantApiControllerTest {

    // Dependency injection and mocking for the required services and objects
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RestaurantService restaurantService;

    @Autowired
    private ObjectMapper objectMapper;

    // Defining constants for reuse in tests
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

    // DTOs and entities used in the tests
    private ApiCreateRestaurantDto validRestaurantDto;
    private Address validAddress;
    private UserEntity validUser;

    @BeforeEach
    public void setUp() {
        // Initialize the test data
        validAddress = createValidAddress();
        validUser = createValidUser();
        validRestaurantDto = createValidRestaurantDto();
    }

    // Helper method to create a valid address
    private Address createValidAddress() {
        return Address.builder()
                .streetAddress(STREET_ADDRESS)
                .city(CITY)
                .postalCode(POSTAL_CODE)
                .build();
    }

    // Helper method to create a valid user
    private UserEntity createValidUser() {
        return UserEntity.builder()
                .name("Test User")
                .email("test@user.com")
                .password("password")
                .build();
    }

    // Helper method to create a valid restaurant DTO
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
        // Cleanup after each test if necessary
    }

    // TEST CASES

    @Test
    @WithMockUser
    public void testCreateRestaurantWithValidData() throws Exception {
        // Mock the service response for creating a restaurant
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

    @Test
    @WithMockUser
    public void testCreateRestaurantWithMissingFields() throws Exception {
        // Test for missing required fields in the request
        ApiCreateRestaurantDto incompleteRestaurantDto = ApiCreateRestaurantDto.builder()
                .name(VALID_NAME)
                .phone(VALID_PHONE)
                .build();

        performPostRequest(incompleteRestaurantDto, 400, ADDRESS_REQUIRED_MESSAGE, null);
    }

    @Test
    @WithMockUser
    public void testCreateRestaurantWithInvalidData() throws Exception {
        // Test for invalid data in the request
        ApiCreateRestaurantDto invalidRestaurantDto = ApiCreateRestaurantDto.builder()
                .name("")
                .phone(INVALID_PHONE)
                .email(INVALID_EMAIL)
                .priceRange(2)
                .address(validRestaurantDto.getAddress())
                .build();

        performPostRequest(invalidRestaurantDto, 400, USER_ID_REQUIRED_MESSAGE, null);
    }

    @Test
    @WithMockUser
    public void testFetchRestaurantsWithoutFilters() throws Exception {
        // Test fetching restaurants without any filters
        ApiRestaurantDto restaurantDto = ApiRestaurantDto.builder()
                .id(1)
                .name(validRestaurantDto.getName())
                .priceRange(validRestaurantDto.getPriceRange())
                .rating(5)
                .build();

        Mockito.when(restaurantService.getRestaurants(null, null)).thenReturn(List.of(restaurantDto));

        performGetRequest(null, null, 200, SUCCESS_MESSAGE, List.of(restaurantDto));
    }

    @Test
    @WithMockUser
    public void testFetchRestaurantsWithRatingFilter() throws Exception {
        // Test fetching restaurants with rating filter
        performGetRequest("5", null, 200, SUCCESS_MESSAGE, List.of());
    }

    @Test
    @WithMockUser
    public void testFetchRestaurantsWithPriceRangeFilter() throws Exception {
        // Test fetching restaurants with price range filter
        ApiRestaurantDto restaurantDto = ApiRestaurantDto.builder()
                .id(1)
                .name(validRestaurantDto.getName())
                .priceRange(validRestaurantDto.getPriceRange())
                .rating(5)
                .build();

        Mockito.when(restaurantService.getRestaurants(null, 2)).thenReturn(List.of(restaurantDto));

        performGetRequest(null, "2", 200, SUCCESS_MESSAGE, List.of(restaurantDto));
    }

    @Test
    @WithMockUser
    public void testFetchRestaurantsWithRatingAndPriceRangeFilters() throws Exception {
        // Test fetching restaurants with both rating and price range filters
        performGetRequest("5", "2", 200, SUCCESS_MESSAGE, List.of());
    }

    @Test
    @WithMockUser
    public void testFetchRestaurantByValidId() throws Exception {
        // Test fetching a restaurant by a valid ID
        ApiRestaurantDto restaurantDto = ApiRestaurantDto.builder()
                .id(1)
                .name(validRestaurantDto.getName())
                .priceRange(validRestaurantDto.getPriceRange())
                .rating(5)
                .build();

        Mockito.when(restaurantService.getRestaurantById(1)).thenReturn(restaurantDto);

        performGetRequestById(1, 200, SUCCESS_MESSAGE, restaurantDto);
    }

    @Test
    @WithMockUser
    public void testFetchRestaurantByInvalidId() throws Exception {
        // Test fetching a restaurant by an invalid ID
        Mockito.when(restaurantService.getRestaurantById(999))
                .thenThrow(new ResourceNotFoundException(NOT_FOUND_MESSAGE));

        performGetRequestById(999, 404, NOT_FOUND_MESSAGE, null);
    }

    @Test
    @WithMockUser
    public void testUpdateRestaurantWithValidData() throws Exception {
        // Test updating a restaurant with valid data
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

    @Test
    @WithMockUser
    public void testUpdateRestaurantWithMissingFields() throws Exception {
        // Test updating a restaurant with missing required fields
        ApiCreateRestaurantDto incompleteRestaurantDto = ApiCreateRestaurantDto.builder()
                .name(UPDATED_NAME)
                .build();

        performPutRequest(1, incompleteRestaurantDto, 400, ADDRESS_REQUIRED_MESSAGE, null);
    }

    @Test
    @WithMockUser
    public void testUpdateRestaurantWithInvalidData() throws Exception {
        // Test updating a restaurant with invalid data
        ApiCreateRestaurantDto invalidRestaurantDto = ApiCreateRestaurantDto.builder()
                .name("")
                .phone(INVALID_PHONE)
                .email(INVALID_EMAIL)
                .priceRange(2)
                .address(validRestaurantDto.getAddress())
                .build();

        performPutRequest(1, invalidRestaurantDto, 400, USER_ID_REQUIRED_MESSAGE, null);
    }

    @Test
    @WithMockUser
    public void testUpdateRestaurantWithNonExistingId() throws Exception {
        // Test updating a restaurant with a non-existing ID
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

    @Test
    @WithMockUser
    public void testDeleteRestaurantWithValidId() throws Exception {
        // Test deleting a restaurant with a valid ID
        ApiRestaurantDto deletedRestaurant = ApiRestaurantDto.builder()
                .id(1)
                .name(VALID_NAME)
                .priceRange(2)
                .rating(5)
                .build();

        Mockito.when(restaurantService.deleteRestaurant(1)).thenReturn(deletedRestaurant);

        performDeleteRequest(1, 200, SUCCESS_MESSAGE, deletedRestaurant);
    }

    @Test
    @WithMockUser
    public void testDeleteRestaurantWithInvalidId() throws Exception {
        // Test deleting a restaurant with an invalid ID
        Mockito.doThrow(new ResourceNotFoundException(NOT_FOUND_MESSAGE))
                .when(restaurantService).deleteRestaurant(999);

        performDeleteRequest(999, 404, NOT_FOUND_MESSAGE, null);
    }

    // HELPER METHODS FOR PERFORMING HTTP REQUESTS

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

    // TEST SECURITY CONFIGURATION

    @Configuration
    @EnableWebSecurity
    public static class TestSecurityConfig {

        @Primary
        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
            // Disable CSRF and allow all requests for testing purposes
            http.csrf().disable()
                    .authorizeRequests().anyRequest().permitAll();
            return http.build();
        }
    }
}
