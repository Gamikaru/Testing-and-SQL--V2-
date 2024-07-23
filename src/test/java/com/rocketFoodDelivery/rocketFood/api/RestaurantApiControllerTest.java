package com.rocketFoodDelivery.rocketFood.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rocketFoodDelivery.rocketFood.controller.api.RestaurantApiController;
import com.rocketFoodDelivery.rocketFood.dtos.ApiAddressDto;
import com.rocketFoodDelivery.rocketFood.dtos.ApiCreateRestaurantDto;
import com.rocketFoodDelivery.rocketFood.dtos.ApiRestaurantDto;
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

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
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

        @Test
        public void testCreateRestaurant_Success() throws Exception {
                ApiCreateRestaurantDto restaurantDto = new ApiCreateRestaurantDto();
                restaurantDto.setUserId(1); // Ensure a valid userId is set
                restaurantDto.setName("Test Restaurant");
                restaurantDto.setPhone("1234567890");
                restaurantDto.setEmail("test@restaurant.com");
                restaurantDto.setPriceRange(2); // Set a valid price range
                restaurantDto.setAddress(new ApiAddressDto(1, "123 Test St", "Test City", "12345")); // Example address

                MvcResult mvcResult = mockMvc.perform(post("/api/restaurants")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(asJsonString(restaurantDto)))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.data.name").value("Test Restaurant"))
                                .andExpect(jsonPath("$.data.phone").value("1234567890"))
                                .andExpect(jsonPath("$.data.email").value("test@restaurant.com"))
                                .andReturn();

                String responseContent = mvcResult.getResponse().getContentAsString();
                JsonNode responseJson = new ObjectMapper().readTree(responseContent);
                int actualId = responseJson.path("data").path("id").asInt();

                assertNotEquals(0, actualId); // Ensure the ID is not 0 or invalid
        }

        @Test
        public void testUpdateRestaurant_Success() throws Exception {
                ApiCreateRestaurantDto restaurantDto = new ApiCreateRestaurantDto();
                restaurantDto.setUserId(1); // Ensure a valid userId is set
                restaurantDto.setName("Updated Restaurant");
                restaurantDto.setPhone("0987654321");
                restaurantDto.setEmail("updated@restaurant.com");
                restaurantDto.setPriceRange(1); // Set a valid price range
                restaurantDto.setAddress(new ApiAddressDto(3, "123 Test St", "Test City", "12345")); // Example address

                // Mock the existing restaurant
                Restaurant existingRestaurant = new Restaurant();
                existingRestaurant.setId(2);
                existingRestaurant.setName("Old Restaurant");
                existingRestaurant.setPhone("1234567890");
                existingRestaurant.setEmail("old@restaurant.com");
                existingRestaurant.setPriceRange(2);
                existingRestaurant.setUserEntity(UserEntity.builder()
                                .id(3)
                                .name("Test User")
                                .email("user@test.com")
                                .password("password")
                                .build());
                existingRestaurant.setAddress(new Address(3, "123 Old St", "Old City", "12345"));

                when(restaurantRepository.findById(2)).thenReturn(Optional.of(existingRestaurant));
                when(restaurantService.updateRestaurant(anyInt(), any(ApiCreateRestaurantDto.class)))
                                .thenAnswer(invocation -> {
                                        int id = invocation.getArgument(0);
                                        ApiCreateRestaurantDto dto = invocation.getArgument(1);
                                        dto.setId(id); // Ensure the returned DTO has the correct ID set
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

                // Log the response content
                String responseContent = mvcResult.getResponse().getContentAsString();
                System.out.println("Response: " + responseContent);

                // Log the status
                int status = mvcResult.getResponse().getStatus();
                System.out.println("Status: " + status);
        }

        @Test
        public void testDeleteRestaurant_Success() throws Exception {
                int restaurantId = 2; // Ensure this ID exists in your mock data

                // Mock the existing restaurant
                Restaurant existingRestaurant = new Restaurant();
                existingRestaurant.setId(restaurantId);
                existingRestaurant.setName("Test Restaurant");
                existingRestaurant.setPhone("1234567890");
                existingRestaurant.setEmail("test@restaurant.com");
                existingRestaurant.setPriceRange(2);
                existingRestaurant.setUserEntity(UserEntity.builder()
                                .id(1)
                                .name("Test User")
                                .email("user@test.com")
                                .password("password")
                                .build());
                existingRestaurant.setAddress(new Address(1, "123 Test St", "Test City", "12345"));

                when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.of(existingRestaurant));
                doNothing().when(restaurantService).deleteRestaurant(restaurantId);

                MvcResult mvcResult = mockMvc.perform(delete("/api/restaurants/{id}", restaurantId)
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.message").value("Restaurant deleted successfully"))
                                .andReturn();

                // Log the response content
                String responseContent = mvcResult.getResponse().getContentAsString();
                System.out.println("Response: " + responseContent);

                // Log the status
                int status = mvcResult.getResponse().getStatus();
                System.out.println("Status: " + status);
        }

        @Test
        public void testDeleteRestaurant_NotFound() throws Exception {
                int restaurantId = 100; // Ensure this ID does not exist in your mock data

                when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.empty());

                MvcResult mvcResult = mockMvc.perform(delete("/api/restaurants/{id}", restaurantId)
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isNotFound())
                                .andExpect(jsonPath("$.message").value("Resource not found"))
                                .andExpect(jsonPath("$.data")
                                                .value("Restaurant with id " + restaurantId + " not found"))
                                .andReturn();

                // Log the response content
                String responseContent = mvcResult.getResponse().getContentAsString();
                System.out.println("Response: " + responseContent);

                // Log the status
                int status = mvcResult.getResponse().getStatus();
                System.out.println("Status: " + status);
        }

        // Helper method to convert objects to JSON string
        private static String asJsonString(final Object obj) {
                try {
                        return new ObjectMapper().writeValueAsString(obj);
                } catch (Exception e) {
                        throw new RuntimeException(e);
                }
        }
}
