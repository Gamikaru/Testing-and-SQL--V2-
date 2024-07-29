package com.rocketFoodDelivery.rocketFood.api;

import com.rocketFoodDelivery.rocketFood.RocketFoodApplication;
import com.fasterxml.jackson.databind.ObjectMapper;
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

        private ApiCreateRestaurantDto validRestaurantDto;
        private Address validAddress;
        private UserEntity validUser;

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

                mockMvc.perform(post(BASE_URI)
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
                                .name(VALID_NAME)
                                .phone(VALID_PHONE)
                                .build();

                mockMvc.perform(post(BASE_URI)
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
                                .phone(INVALID_PHONE)
                                .email(INVALID_EMAIL)
                                .priceRange(2)
                                .address(validRestaurantDto.getAddress())
                                .build();

                mockMvc.perform(post(BASE_URI)
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

                mockMvc.perform(get(BASE_URI))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.data", hasSize(1)))
                                .andExpect(jsonPath("$.data[0].name", is(restaurantDto.getName())))
                                .andExpect(jsonPath("$.data[0].price_range", is(restaurantDto.getPriceRange())));
        }

        @Test
        @WithMockUser
        public void testFetchRestaurantsWithRatingFilter() throws Exception {
                mockMvc.perform(get(BASE_URI).param("rating", "5"))
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

                mockMvc.perform(get(BASE_URI).param("priceRange", "2"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.data", hasSize(1)))
                                .andExpect(jsonPath("$.data[0].name", is(restaurantDto.getName())))
                                .andExpect(jsonPath("$.data[0].price_range", is(restaurantDto.getPriceRange())));
        }

        @Test
        @WithMockUser
        public void testFetchRestaurantsWithRatingAndPriceRangeFilters() throws Exception {
                mockMvc.perform(get(BASE_URI)
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

                mockMvc.perform(get(BASE_URI + "/{id}", 1))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.data.name", is(restaurantDto.getName())))
                                .andExpect(jsonPath("$.data.price_range", is(restaurantDto.getPriceRange())));
        }

        @Test
        @WithMockUser
        public void testFetchRestaurantByInvalidId() throws Exception {
                Mockito.when(restaurantService.getRestaurantById(999))
                                .thenThrow(new ResourceNotFoundException("Restaurant with id 999 not found"));

                mockMvc.perform(get(BASE_URI + "/{id}", 999))
                                .andExpect(status().isNotFound())
                                .andExpect(jsonPath("$.message", is("Restaurant with id 999 not found")));
        }

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

                Mockito.when(restaurantService.updateRestaurant(Mockito.eq(1),
                                Mockito.any(ApiCreateRestaurantDto.class)))
                                .thenReturn(updatedRestaurant);

                mockMvc.perform(put(BASE_URI + "/{id}", 1)
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
                                .name(UPDATED_NAME)
                                .build();

                mockMvc.perform(put(BASE_URI + "/{id}", 1)
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
                                .phone(INVALID_PHONE)
                                .email(INVALID_EMAIL)
                                .priceRange(2)
                                .address(validRestaurantDto.getAddress())
                                .build();

                mockMvc.perform(put(BASE_URI + "/{id}", 1)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(invalidRestaurantDto)))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.message", is("Invalid or missing parameters")));
        }

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

                Mockito.when(restaurantService.updateRestaurant(Mockito.eq(999),
                                Mockito.any(ApiCreateRestaurantDto.class)))
                                .thenThrow(new ResourceNotFoundException("Restaurant with id 999 not found"));

                mockMvc.perform(put(BASE_URI + "/{id}", 999)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(updatedRestaurantDto)))
                                .andExpect(status().isNotFound())
                                .andExpect(jsonPath("$.message", is("Restaurant with id 999 not found")));
        }

        @Test
        @WithMockUser
        public void testDeleteRestaurantWithValidId() throws Exception {
                Mockito.doNothing().when(restaurantService).deleteRestaurant(1);

                mockMvc.perform(delete(BASE_URI + "/{id}", 1))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.message", is("Success")))
                                .andExpect(jsonPath("$.data", is("Restaurant deleted successfully")));
        }

        @Test
        @WithMockUser
        public void testDeleteRestaurantWithInvalidId() throws Exception {
                Mockito.doThrow(new ResourceNotFoundException("Restaurant with id 999 not found"))
                                .when(restaurantService).deleteRestaurant(999);

                mockMvc.perform(delete(BASE_URI + "/{id}", 999))
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
