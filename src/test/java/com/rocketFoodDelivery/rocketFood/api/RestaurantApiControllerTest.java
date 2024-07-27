package com.rocketFoodDelivery.rocketFood.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rocketFoodDelivery.rocketFood.controller.api.RestaurantApiController;
import com.rocketFoodDelivery.rocketFood.dtos.ApiAddressDto;
import com.rocketFoodDelivery.rocketFood.dtos.ApiCreateRestaurantDto;
import com.rocketFoodDelivery.rocketFood.dtos.ApiRestaurantDto;
import com.rocketFoodDelivery.rocketFood.models.Address;
import com.rocketFoodDelivery.rocketFood.models.Order;
import com.rocketFoodDelivery.rocketFood.models.Restaurant;
import com.rocketFoodDelivery.rocketFood.models.UserEntity;
import com.rocketFoodDelivery.rocketFood.repository.OrderRepository;
import com.rocketFoodDelivery.rocketFood.repository.RestaurantRepository;
import com.rocketFoodDelivery.rocketFood.repository.UserRepository;
import com.rocketFoodDelivery.rocketFood.service.RestaurantService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
@Transactional
public class RestaurantApiControllerTest {

        @InjectMocks
        private RestaurantApiController restaurantController;

        @MockBean
        private RestaurantService restaurantService;

        @MockBean
        private UserRepository userRepository;

        @MockBean
        private RestaurantRepository restaurantRepository;

        @Autowired
        private MockMvc mockMvc;

        @MockBean
        private OrderRepository orderRepository;

        @Autowired
        private Environment environment;

        @BeforeEach
        public void setUp() {
                System.out.println("Active profiles: " + Arrays.toString(environment.getActiveProfiles()));
                System.out.println("Using datasource URL: " + environment.getProperty("spring.datasource.url"));
                reset(restaurantService, userRepository, restaurantRepository, orderRepository);

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

                Restaurant testRestaurant1 = Restaurant.builder()
                                .id(1)
                                .name("Restaurant One")
                                .phone("1234567890")
                                .email("restaurant1@test.com")
                                .priceRange(1)
                                .userEntity(testUser)
                                .address(testAddress)
                                .build();

                Restaurant testRestaurant2 = Restaurant.builder()
                                .id(2)
                                .name("Restaurant Two")
                                .phone("0987654321")
                                .email("restaurant2@test.com")
                                .priceRange(1)
                                .userEntity(testUser)
                                .address(testAddress)
                                .build();

                when(userRepository.findById(1)).thenReturn(Optional.of(testUser));
                when(restaurantRepository.findById(1)).thenReturn(Optional.of(testRestaurant1));
                when(restaurantRepository.findById(2)).thenReturn(Optional.of(testRestaurant2));
                when(restaurantRepository.save(any(Restaurant.class))).thenReturn(testRestaurant1);

                when(restaurantService.updateRestaurant(anyInt(), any(ApiCreateRestaurantDto.class)))
                                .thenAnswer(invocation -> {
                                        int id = invocation.getArgument(0);
                                        ApiCreateRestaurantDto dto = invocation.getArgument(1);
                                        dto.setId(id);
                                        return Optional.of(dto);
                                });

                doNothing().when(restaurantService).deleteRestaurant(anyInt());
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

                System.out.println("Request payload: " + asJsonString(restaurantDto));

                when(restaurantService.createRestaurant(any(ApiCreateRestaurantDto.class)))
                                .thenAnswer(invocation -> {
                                        ApiCreateRestaurantDto dto = invocation.getArgument(0);
                                        dto.setId(1); // Mock the id assignment after creation
                                        return Optional.of(dto);
                                });

                MvcResult mvcResult = mockMvc.perform(post("/api/restaurants")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(asJsonString(restaurantDto)))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.data.name").value("Test Restaurant"))
                                .andExpect(jsonPath("$.data.phone").value("1234567890"))
                                .andExpect(jsonPath("$.data.email").value("test@restaurant.com"))
                                .andReturn();

                String responseContent = mvcResult.getResponse().getContentAsString();
                System.out.println("Response: " + responseContent);

                JsonNode responseJson = new ObjectMapper().readTree(responseContent);
                int actualId = responseJson.path("data").path("id").asInt();

                assertNotEquals(0, actualId);

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
                                .andExpect(jsonPath("$.data")
                                                .value("Restaurant with id " + restaurantId + " not found"))
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

                System.out.println("Request payload: " + asJsonString(restaurantDto));

                MvcResult mvcResult = mockMvc.perform(post("/api/restaurants")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(asJsonString(restaurantDto)))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.message").value("Validation failed"))
                                .andReturn();

                String responseContent = mvcResult.getResponse().getContentAsString();
                System.out.println("Response: " + responseContent);

                int status = mvcResult.getResponse().getStatus();
                System.out.println("Status: " + status);
        }

        @Test
        public void testGetRestaurants_Success() throws Exception {
                ApiRestaurantDto restaurant1 = ApiRestaurantDto.builder()
                                .id(1)
                                .name("Restaurant One")
                                .priceRange(1)
                                .rating(5)
                                .build();

                ApiRestaurantDto restaurant2 = ApiRestaurantDto.builder()
                                .id(2)
                                .name("Restaurant Two")
                                .priceRange(1)
                                .rating(4)
                                .build();

                List<ApiRestaurantDto> restaurants = Arrays.asList(restaurant1, restaurant2);

                System.out.println("Mocking RestaurantService.findRestaurantsByRatingAndPriceRange to return: "
                                + restaurants);

                when(restaurantService.findRestaurantsByRatingAndPriceRange(any(), any())).thenReturn(restaurants);

                MvcResult mvcResult = mockMvc.perform(get("/api/restaurants")
                                .param("rating", "5")
                                .param("price_range", "1")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.data.length()").value(2))
                                .andExpect(jsonPath("$.data[0].name").value("Restaurant One"))
                                .andExpect(jsonPath("$.data[0].rating").value(5))
                                .andExpect(jsonPath("$.data[1].name").value("Restaurant Two"))
                                .andExpect(jsonPath("$.data[1].rating").value(4))
                                .andReturn();

                String responseContent = mvcResult.getResponse().getContentAsString();
                System.out.println("Response: " + responseContent);

                JsonNode responseJson = new ObjectMapper().readTree(responseContent);
                assertEquals(2, responseJson.path("data").size());
                assertEquals("Restaurant One", responseJson.path("data").get(0).path("name").asText());
                assertEquals(5, responseJson.path("data").get(0).path("rating").asInt());
                assertEquals("Restaurant Two", responseJson.path("data").get(1).path("name").asText());
                assertEquals(4, responseJson.path("data").get(1).path("rating").asInt());
        }

        @Test
        public void testGetRestaurants_EmptyResult() throws Exception {
                List<ApiRestaurantDto> emptyRestaurants = Arrays.asList();

                System.out.println("Mocking RestaurantService.findRestaurantsByRatingAndPriceRange to return: "
                                + emptyRestaurants);

                when(restaurantService.findRestaurantsByRatingAndPriceRange(any(), any())).thenReturn(emptyRestaurants);

                MvcResult mvcResult = mockMvc.perform(get("/api/restaurants")
                                .param("rating", "1")
                                .param("price_range", "3")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.data.length()").value(0))
                                .andReturn();

                String responseContent = mvcResult.getResponse().getContentAsString();
                System.out.println("Response: " + responseContent);

                JsonNode responseJson = new ObjectMapper().readTree(responseContent);
                assertEquals(0, responseJson.path("data").size());
        }

        @Test
        public void testGetAllRestaurants_Success() throws Exception {
                ApiRestaurantDto restaurant1 = ApiRestaurantDto.builder()
                                .id(1)
                                .name("Restaurant One")
                                .priceRange(1)
                                .rating(5)
                                .build();

                ApiRestaurantDto restaurant2 = ApiRestaurantDto.builder()
                                .id(2)
                                .name("Restaurant Two")
                                .priceRange(1)
                                .rating(4)
                                .build();

                List<ApiRestaurantDto> restaurants = Arrays.asList(restaurant1, restaurant2);

                when(restaurantService.findRestaurantsByRatingAndPriceRange(null, null)).thenReturn(restaurants);

                MvcResult mvcResult = mockMvc.perform(get("/api/restaurants")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.data.length()").value(2))
                                .andExpect(jsonPath("$.data[0].name").value("Restaurant One"))
                                .andExpect(jsonPath("$.data[0].rating").value(5))
                                .andExpect(jsonPath("$.data[1].name").value("Restaurant Two"))
                                .andExpect(jsonPath("$.data[1].rating").value(4))
                                .andReturn();

                String responseContent = mvcResult.getResponse().getContentAsString();
                System.out.println("Response: " + responseContent);

                JsonNode responseJson = new ObjectMapper().readTree(responseContent);
                assertEquals(2, responseJson.path("data").size());
                assertEquals("Restaurant One", responseJson.path("data").get(0).path("name").asText());
                assertEquals(5, responseJson.path("data").get(0).path("rating").asInt());
                assertEquals("Restaurant Two", responseJson.path("data").get(1).path("name").asText());
                assertEquals(4, responseJson.path("data").get(1).path("rating").asInt());
        }

        private static String asJsonString(final Object obj) {
                try {
                        return new ObjectMapper().writeValueAsString(obj);
                } catch (Exception e) {
                        throw new RuntimeException(e);
                }
        }
}
