package com.rocketFoodDelivery.rocketFood.api;

import com.rocketFoodDelivery.rocketFood.RocketFoodApplication;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rocketFoodDelivery.rocketFood.dtos.ApiAddressDto;
import com.rocketFoodDelivery.rocketFood.dtos.ApiCreateRestaurantDto;
import com.rocketFoodDelivery.rocketFood.dtos.ApiRestaurantDto;
import com.rocketFoodDelivery.rocketFood.exception.ResourceNotFoundException;
import com.rocketFoodDelivery.rocketFood.models.Address;
import com.rocketFoodDelivery.rocketFood.models.Restaurant;
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

        @Autowired
        private MockMvc mockMvc;

        @MockBean
        private RestaurantService restaurantService;

        @Autowired
        private ObjectMapper objectMapper;

        private ApiCreateRestaurantDto validRestaurantDto;
        private Address validAddress;
        private UserEntity validUser;

        @BeforeEach
        public void setUp() {
                validAddress = Address.builder()
                                .streetAddress("123 Main St")
                                .city("Springfield")
                                .postalCode("12345")
                                .build();

                validUser = UserEntity.builder()
                                .name("Test User")
                                .email("test@user.com")
                                .password("password")
                                .build();

                validRestaurantDto = ApiCreateRestaurantDto.builder()
                                .name("Test Restaurant")
                                .phone("123-456-7890")
                                .email("test@restaurant.com")
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

        @Test
        @WithMockUser
        public void testCreateRestaurantWithValidData() throws Exception {
                ApiRestaurantDto createdRestaurant = ApiRestaurantDto.builder()
                                .id(1)
                                .name(validRestaurantDto.getName())
                                .priceRange(validRestaurantDto.getPriceRange())
                                .rating(5) // Placeholder rating
                                .build();

                Mockito.when(restaurantService.createRestaurant(Mockito.any(ApiCreateRestaurantDto.class)))
                                .thenReturn(createdRestaurant);

                mockMvc.perform(post("/api/restaurants")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(validRestaurantDto)))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.data.name").value(validRestaurantDto.getName()))
                                .andExpect(jsonPath("$.data.price_range").value(validRestaurantDto.getPriceRange()))
                                .andExpect(jsonPath("$.data.rating").value(5));
        }

        @Test
        @WithMockUser
        public void testCreateRestaurantWithMissingFields() throws Exception {
                ApiCreateRestaurantDto incompleteRestaurantDto = ApiCreateRestaurantDto.builder()
                                .name("Test Restaurant")
                                .phone("123-456-7890")
                                .build();

                mockMvc.perform(post("/api/restaurants")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(incompleteRestaurantDto)))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.message", is("Invalid or missing parameters")));
        }

        @Test
        @WithMockUser
        public void testCreateRestaurantWithInvalidData() throws Exception {
                ApiCreateRestaurantDto invalidRestaurantDto = ApiCreateRestaurantDto.builder()
                                .name("")
                                .phone("invalid-phone")
                                .email("invalid-email")
                                .priceRange(2)
                                .address(validRestaurantDto.getAddress())
                                .build();

                mockMvc.perform(post("/api/restaurants")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(invalidRestaurantDto)))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.message", is("Invalid or missing parameters")));
        }

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

                mockMvc.perform(get("/api/restaurants"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.data", hasSize(1)))
                                .andExpect(jsonPath("$.data[0].name", is(restaurantDto.getName())))
                                .andExpect(jsonPath("$.data[0].price_range", is(restaurantDto.getPriceRange())));
        }

        @Test
        @WithMockUser
        public void testFetchRestaurantsWithRatingFilter() throws Exception {
                mockMvc.perform(get("/api/restaurants").param("rating", "5"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.data", is(empty())));
        }

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

                mockMvc.perform(get("/api/restaurants").param("priceRange", "2"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.data", hasSize(1)))
                                .andExpect(jsonPath("$.data[0].name", is(restaurantDto.getName())))
                                .andExpect(jsonPath("$.data[0].price_range", is(restaurantDto.getPriceRange())));
        }

        @Test
        @WithMockUser
        public void testFetchRestaurantsWithRatingAndPriceRangeFilters() throws Exception {
                mockMvc.perform(get("/api/restaurants")
                                .param("rating", "5")
                                .param("priceRange", "2"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.data", is(empty())));
        }

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

                mockMvc.perform(get("/api/restaurants/{id}", 1))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.data.name", is(restaurantDto.getName())))
                                .andExpect(jsonPath("$.data.price_range", is(restaurantDto.getPriceRange())));
        }

        @Test
        @WithMockUser
        public void testFetchRestaurantByInvalidId() throws Exception {
                Mockito.when(restaurantService.getRestaurantById(999))
                                .thenThrow(new ResourceNotFoundException("Restaurant with id 999 not found"));

                mockMvc.perform(get("/api/restaurants/{id}", 999))
                                .andExpect(status().isNotFound())
                                .andExpect(jsonPath("$.message", is("Restaurant with id 999 not found")));
        }

        @Test
        @WithMockUser
        public void testUpdateRestaurantWithValidData() throws Exception {
                ApiCreateRestaurantDto updatedRestaurantDto = ApiCreateRestaurantDto.builder()
                                .name("Updated Restaurant")
                                .phone("098-765-4321")
                                .email("updated@restaurant.com")
                                .priceRange(3)
                                .address(validRestaurantDto.getAddress())
                                .build();

                ApiRestaurantDto updatedRestaurant = ApiRestaurantDto.builder()
                                .id(1)
                                .name(updatedRestaurantDto.getName())
                                .priceRange(updatedRestaurantDto.getPriceRange())
                                .rating(5)
                                .build();

                Mockito.when(restaurantService.updateRestaurant(Mockito.eq(1),
                                Mockito.any(ApiCreateRestaurantDto.class)))
                                .thenReturn(updatedRestaurant);

                mockMvc.perform(put("/api/restaurants/{id}", 1)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(updatedRestaurantDto)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.data.name", is(updatedRestaurantDto.getName())))
                                .andExpect(jsonPath("$.data.price_range", is(updatedRestaurantDto.getPriceRange())));
        }

        @Test
        @WithMockUser
        public void testUpdateRestaurantWithMissingFields() throws Exception {
                ApiCreateRestaurantDto incompleteRestaurantDto = ApiCreateRestaurantDto.builder()
                                .name("Updated Restaurant")
                                .build();

                mockMvc.perform(put("/api/restaurants/{id}", 1)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(incompleteRestaurantDto)))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.message", is("Invalid or missing parameters")));
        }

        @Test
        @WithMockUser
        public void testUpdateRestaurantWithInvalidData() throws Exception {
                ApiCreateRestaurantDto invalidRestaurantDto = ApiCreateRestaurantDto.builder()
                                .name("")
                                .phone("invalid-phone")
                                .email("invalid-email")
                                .priceRange(2)
                                .address(validRestaurantDto.getAddress())
                                .build();

                mockMvc.perform(put("/api/restaurants/{id}", 1)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(invalidRestaurantDto)))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.message", is("Invalid or missing parameters")));
        }

        @Test
        @WithMockUser
        public void testUpdateRestaurantWithNonExistingId() throws Exception {
                ApiCreateRestaurantDto updatedRestaurantDto = ApiCreateRestaurantDto.builder()
                                .name("Updated Restaurant")
                                .phone("098-765-4321")
                                .email("updated@restaurant.com")
                                .priceRange(3)
                                .address(validRestaurantDto.getAddress())
                                .build();

                Mockito.when(restaurantService.updateRestaurant(Mockito.eq(999),
                                Mockito.any(ApiCreateRestaurantDto.class)))
                                .thenThrow(new ResourceNotFoundException("Restaurant with id 999 not found"));

                mockMvc.perform(put("/api/restaurants/{id}", 999)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(updatedRestaurantDto)))
                                .andExpect(status().isNotFound())
                                .andExpect(jsonPath("$.message", is("Restaurant with id 999 not found")));
        }

        @Test
        @WithMockUser
        public void testDeleteRestaurantWithValidId() throws Exception {
                Mockito.doNothing().when(restaurantService).deleteRestaurant(1);

                mockMvc.perform(delete("/api/restaurants/{id}", 1))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.message", is("Success")))
                                .andExpect(jsonPath("$.data", is("Restaurant deleted successfully")));
        }

        @Test
        @WithMockUser
        public void testDeleteRestaurantWithInvalidId() throws Exception {
                Mockito.doThrow(new ResourceNotFoundException("Restaurant with id 999 not found"))
                                .when(restaurantService).deleteRestaurant(999);

                mockMvc.perform(delete("/api/restaurants/{id}", 999))
                                .andExpect(status().isNotFound())
                                .andExpect(jsonPath("$.message", is("Restaurant with id 999 not found")));
        }

        @Configuration
        @EnableWebSecurity
        public static class TestSecurityConfig {

                @Primary
                @Bean
                public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                        http.csrf().disable()
                                        .authorizeRequests().anyRequest().permitAll();
                        return http.build();
                }
        }
}
