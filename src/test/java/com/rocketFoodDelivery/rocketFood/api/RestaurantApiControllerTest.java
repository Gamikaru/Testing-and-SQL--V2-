package com.rocketFoodDelivery.rocketFood.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rocketFoodDelivery.rocketFood.controller.api.RestaurantApiController;
import com.rocketFoodDelivery.rocketFood.dtos.ApiAddressDto;
import com.rocketFoodDelivery.rocketFood.dtos.ApiCreateRestaurantDto;
import com.rocketFoodDelivery.rocketFood.models.Address;
import com.rocketFoodDelivery.rocketFood.models.Restaurant;
import com.rocketFoodDelivery.rocketFood.models.UserEntity;
import com.rocketFoodDelivery.rocketFood.repository.RestaurantRepository;
import com.rocketFoodDelivery.rocketFood.repository.UserRepository;
import com.rocketFoodDelivery.rocketFood.service.RestaurantService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
@Transactional
public class RestaurantApiControllerTest {

    @InjectMocks
    private RestaurantApiController restaurantController;

    @Mock
    private RestaurantService restaurantService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RestaurantRepository restaurantRepository;

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        // Reset mocks before each test
        reset(restaurantService, userRepository, restaurantRepository);

        // Set up mock data for tests
        UserEntity testUser = UserEntity.builder()
                .id(1)
                .name("Test User")
                .email("user@test.com")
                .password("password")
                .build();

        Address testAddress = Address.builder()
                .id(1)
                .streetAddress("123 Test St")
                .city("Test City")
                .postalCode("12345")
                .build();

        Restaurant testRestaurant = Restaurant.builder()
                .id(1)
                .name("Test Restaurant")
                .phone("1234567890")
                .email("test@restaurant.com")
                .priceRange(2)
                .userEntity(testUser)
                .address(testAddress)
                .build();

        when(userRepository.findById(1)).thenReturn(Optional.of(testUser));
        when(restaurantRepository.findById(1)).thenReturn(Optional.of(testRestaurant));
        when(restaurantRepository.save(any(Restaurant.class))).thenReturn(testRestaurant);
    }

    @Test
    public void testCreateRestaurant_Success() throws Exception {
        ApiCreateRestaurantDto restaurantDto = new ApiCreateRestaurantDto();
        restaurantDto.setUserId(1);
        restaurantDto.setName("Test Restaurant");
        restaurantDto.setPhone("1234567890");
        restaurantDto.setEmail("test@restaurant.com");
        restaurantDto.setPriceRange(2);
        restaurantDto.setAddress(new ApiAddressDto(1, "123 Test St", "Test City", "12345"));

        // Log the request payload
        System.out.println("Request payload: " + asJsonString(restaurantDto));

        MvcResult mvcResult = mockMvc.perform(post("/api/restaurants")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(restaurantDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.name").value("Test Restaurant"))
                .andExpect(jsonPath("$.data.phone").value("1234567890"))
                .andExpect(jsonPath("$.data.email").value("test@restaurant.com"))
                .andReturn();

        // Log the response
        String responseContent = mvcResult.getResponse().getContentAsString();
        System.out.println("Response: " + responseContent);

        JsonNode responseJson = new ObjectMapper().readTree(responseContent);
        int actualId = responseJson.path("data").path("id").asInt();

        assertNotEquals(0, actualId);

        // Add additional assertions if necessary
        assertEquals("Test Restaurant", responseJson.path("data").path("name").asText());
        assertEquals("1234567890", responseJson.path("data").path("phone").asText());
        assertEquals("test@restaurant.com", responseJson.path("data").path("email").asText());
    }

    @Test
    public void testUpdateRestaurant_Success() throws Exception {
        ApiCreateRestaurantDto restaurantDto = new ApiCreateRestaurantDto();
        restaurantDto.setUserId(1);
        restaurantDto.setName("Updated Restaurant");
        restaurantDto.setPhone("0987654321");
        restaurantDto.setEmail("updated@restaurant.com");
        restaurantDto.setPriceRange(1);
        restaurantDto.setAddress(new ApiAddressDto(3, "123 Test St", "Test City", "12345"));

        Restaurant existingRestaurant = Restaurant.builder()
                .id(2)
                .name("Old Restaurant")
                .phone("1234567890")
                .email("old@restaurant.com")
                .priceRange(2)
                .userEntity(UserEntity.builder()
                        .id(3)
                        .name("Test User")
                        .email("user@test.com")
                        .password("password")
                        .build())
                .address(Address.builder()
                        .id(3)
                        .streetAddress("123 Old St")
                        .city("Old City")
                        .postalCode("12345")
                        .build())
                .build();

        when(restaurantRepository.findById(2)).thenReturn(Optional.of(existingRestaurant));
        when(restaurantService.updateRestaurant(anyInt(), any(ApiCreateRestaurantDto.class)))
                .thenAnswer(invocation -> {
                    int id = invocation.getArgument(0);
                    ApiCreateRestaurantDto dto = invocation.getArgument(1);
                    dto.setId(id);
                    return Optional.of(dto);
                });

        MvcResult mvcResult = mockMvc.perform(put("/api/restaurants/{id}", 2)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(restaurantDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value("Updated Restaurant"))
                .andExpect(jsonPath("$.data.phone").value("0987654321"))
                .andExpect(jsonPath("$.data.email").value("updated@restaurant.com"))
                .andReturn();

        String responseContent = mvcResult.getResponse().getContentAsString();
        System.out.println("Response: " + responseContent);

        int status = mvcResult.getResponse().getStatus();
        System.out.println("Status: " + status);
    }

    @Test
    public void testDeleteRestaurant_Success() throws Exception {
        int restaurantId = 2;

        Restaurant existingRestaurant = Restaurant.builder()
                .id(restaurantId)
                .name("Test Restaurant")
                .phone("1234567890")
                .email("test@restaurant.com")
                .priceRange(2)
                .userEntity(UserEntity.builder()
                        .id(1)
                        .name("Test User")
                        .email("user@test.com")
                        .password("password")
                        .build())
                .address(Address.builder()
                        .id(1)
                        .streetAddress("123 Test St")
                        .city("Test City")
                        .postalCode("12345")
                        .build())
                .build();

        when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.of(existingRestaurant));
        doNothing().when(restaurantService).deleteRestaurant(restaurantId);

        MvcResult mvcResult = mockMvc.perform(delete("/api/restaurants/{id}", restaurantId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Restaurant deleted successfully"))
                .andReturn();

        String responseContent = mvcResult.getResponse().getContentAsString();
        System.out.println("Response: " + responseContent);

        int status = mvcResult.getResponse().getStatus();
        System.out.println("Status: " + status);
    }

    @Test
    public void testDeleteRestaurant_NotFound() throws Exception {
        int restaurantId = 100;

        when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.empty());

        MvcResult mvcResult = mockMvc.perform(delete("/api/restaurants/{id}", restaurantId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Resource not found"))
                .andExpect(jsonPath("$.data").value("Restaurant with id " + restaurantId + " not found"))
                .andReturn();

        String responseContent = mvcResult.getResponse().getContentAsString();
        System.out.println("Response: " + responseContent);

        int status = mvcResult.getResponse().getStatus();
        System.out.println("Status: " + status);
    }

    @Test
    public void testUpdateRestaurant_NotFound() throws Exception {
        ApiCreateRestaurantDto restaurantDto = new ApiCreateRestaurantDto();
        restaurantDto.setUserId(1);
        restaurantDto.setName("Non-existent Restaurant");
        restaurantDto.setPhone("0000000000");
        restaurantDto.setEmail("nonexistent@restaurant.com");
        restaurantDto.setPriceRange(1);
        restaurantDto.setAddress(new ApiAddressDto(1, "123 Test St", "Test City", "12345"));

        when(restaurantRepository.findById(100)).thenReturn(Optional.empty());

        MvcResult mvcResult = mockMvc.perform(put("/api/restaurants/{id}", 100)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(restaurantDto)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Resource not found"))
                .andExpect(jsonPath("$.data").value("Restaurant with id 100 not found"))
                .andReturn();

        String responseContent = mvcResult.getResponse().getContentAsString();
        System.out.println("Response: " + responseContent);

        int status = mvcResult.getResponse().getStatus();
        System.out.println("Status: " + status);
    }

    @Test
    public void testCreateRestaurant_InvalidData() throws Exception {
        ApiCreateRestaurantDto restaurantDto = new ApiCreateRestaurantDto();
        restaurantDto.setUserId(1);
        restaurantDto.setName(""); // Invalid name
        restaurantDto.setPhone("1234567890");
        restaurantDto.setEmail("invalid-email"); // Invalid email format
        restaurantDto.setPriceRange(0); // Invalid price range
        restaurantDto.setAddress(new ApiAddressDto(1, "123 Test St", "Test City", "12345"));

        // Log the request payload
        System.out.println("Request payload: " + asJsonString(restaurantDto));

        MvcResult mvcResult = mockMvc.perform(post("/api/restaurants")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(restaurantDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andReturn();

        // Log the response
        String responseContent = mvcResult.getResponse().getContentAsString();
        System.out.println("Response: " + responseContent);

        int status = mvcResult.getResponse().getStatus();
        System.out.println("Status: " + status);
    }

    private static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
