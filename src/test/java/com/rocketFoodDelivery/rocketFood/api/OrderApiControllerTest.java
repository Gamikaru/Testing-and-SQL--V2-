package com.rocketFoodDelivery.rocketFood.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rocketFoodDelivery.rocketFood.RocketFoodApplication;
import com.rocketFoodDelivery.rocketFood.controller.api.OrderApiController;
import com.rocketFoodDelivery.rocketFood.dtos.*;
import com.rocketFoodDelivery.rocketFood.exception.ResourceNotFoundException;
import com.rocketFoodDelivery.rocketFood.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the OrderApiController.
 */
@SpringBootTest(classes = RocketFoodApplication.class)
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
public class OrderApiControllerTest {

        private static final String BASE_URL = "/api/order";
        private static final String SUCCESS_MESSAGE = "Success";
        private static final String NOT_FOUND_MESSAGE = "Order with id 1 not found";

        @Autowired
        private MockMvc mockMvc;

        @MockBean
        private OrderService orderService;

        @Autowired
        private ObjectMapper objectMapper;

        @InjectMocks
        private OrderApiController orderApiController;

        private ApiOrderRequestDto orderRequestDto;
        private ApiOrderDto newOrder;
        private ApiOrderStatusDto orderStatusDto;
        private Integer orderId;

        /**
         * Setup method to initialize test data before each test.
         */
        @BeforeEach
        public void setup() {
                MockitoAnnotations.openMocks(this);
                setupTestData();
        }

        private void setupTestData() {
                orderId = 1;

                orderRequestDto = new ApiOrderRequestDto();
                orderRequestDto.setCustomer_id(1);
                orderRequestDto.setRestaurant_id(1);
                orderRequestDto.setProducts(Arrays.asList(
                                new ApiProductOrderRequestDto(1, 2),
                                new ApiProductOrderRequestDto(2, 1)));

                newOrder = ApiOrderDto.builder()
                                .id(1)
                                .customer_id(1)
                                .restaurant_id(1)
                                .status("in progress")
                                .total_cost(350)
                                .products(Arrays.asList(
                                                new ApiProductForOrderApiDto(1, "Pasta with Tomato and Basil", 2, 100,
                                                                200),
                                                new ApiProductForOrderApiDto(2, "Tuna Sashimi", 1, 150, 150)))
                                .build();

                orderStatusDto = ApiOrderStatusDto.builder().status("delivered").build();
        }

        /**
         * Tests changing the status of an order.
         * 
         * @throws Exception if an error occurs during the test.
         */
        @Test
        @WithMockUser
        public void testChangeOrderStatus() throws Exception {
                when(orderService.changeOrderStatus(orderId, orderStatusDto)).thenReturn(orderStatusDto.getStatus());

                mockMvc.perform(post(BASE_URL + "/{order_id}/status", orderId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(orderStatusDto)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.message").value(SUCCESS_MESSAGE))
                                .andExpect(jsonPath("$.data.status").value(orderStatusDto.getStatus()));

                verify(orderService, times(1)).changeOrderStatus(orderId, orderStatusDto);
        }

        /**
         * Tests changing the status of an order when the order is not found.
         * 
         * @throws Exception if an error occurs during the test.
         */
        @Test
        @WithMockUser
        public void testChangeOrderStatus_NotFound() throws Exception {
                when(orderService.changeOrderStatus(orderId, orderStatusDto))
                                .thenThrow(new ResourceNotFoundException(NOT_FOUND_MESSAGE));

                mockMvc.perform(post(BASE_URL + "/{order_id}/status", orderId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(orderStatusDto)))
                                .andExpect(status().isNotFound())
                                .andExpect(jsonPath("$.message").value(NOT_FOUND_MESSAGE))
                                .andExpect(jsonPath("$.data").doesNotExist());

                verify(orderService, times(1)).changeOrderStatus(orderId, orderStatusDto);
        }

        /**
         * Tests retrieving orders by customer ID.
         * 
         * @throws Exception if an error occurs during the test.
         */
        @Test
        @WithMockUser
        public void testGetOrdersByTypeAndId_Customer() throws Exception {
                List<ApiOrderDto> orders = Arrays.asList(
                                ApiOrderDto.builder().id(1).customer_id(1).status("in progress").build(),
                                ApiOrderDto.builder().id(2).customer_id(1).status("delivered").build());

                when(orderService.getOrdersByTypeAndId("customer", 1)).thenReturn(orders);

                mockMvc.perform(get(BASE_URL)
                                .param("type", "customer")
                                .param("id", String.valueOf(1))
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.message").value(SUCCESS_MESSAGE))
                                .andExpect(jsonPath("$.data", hasSize(orders.size())))
                                .andExpect(jsonPath("$.data[0].id").value(orders.get(0).getId()))
                                .andExpect(jsonPath("$.data[0].customer_id").value(orders.get(0).getCustomer_id()))
                                .andExpect(jsonPath("$.data[0].status").value(orders.get(0).getStatus()))
                                .andExpect(jsonPath("$.data[1].id").value(orders.get(1).getId()))
                                .andExpect(jsonPath("$.data[1].customer_id").value(orders.get(1).getCustomer_id()))
                                .andExpect(jsonPath("$.data[1].status").value(orders.get(1).getStatus()));

                verify(orderService, times(1)).getOrdersByTypeAndId("customer", 1);
        }

        /**
         * Tests creating a new order.
         * 
         * @throws Exception if an error occurs during the test.
         */
        @Test
        @WithMockUser
        public void testCreateOrder() throws Exception {
                when(orderService.createOrder(any(ApiOrderRequestDto.class))).thenReturn(newOrder);

                mockMvc.perform(post(BASE_URL)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(orderRequestDto)))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.message").value(SUCCESS_MESSAGE))
                                .andExpect(jsonPath("$.data.id").value(newOrder.getId()))
                                .andExpect(jsonPath("$.data.customer_id").value(newOrder.getCustomer_id()))
                                .andExpect(jsonPath("$.data.restaurant_id").value(newOrder.getRestaurant_id()))
                                .andExpect(jsonPath("$.data.status").value(newOrder.getStatus()))
                                .andExpect(jsonPath("$.data.total_cost").value(newOrder.getTotal_cost()));

                verify(orderService, times(1)).createOrder(any(ApiOrderRequestDto.class));
        }

        /**
         * Tests creating a new order when the customer is not found.
         * 
         * @throws Exception if an error occurs during the test.
         */
        @Test
        @WithMockUser
        public void testCreateOrder_Exception() throws Exception {
                when(orderService.createOrder(any(ApiOrderRequestDto.class)))
                                .thenThrow(new ResourceNotFoundException("Customer not found"));

                mockMvc.perform(post(BASE_URL)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(orderRequestDto)))
                                .andExpect(status().isNotFound())
                                .andExpect(jsonPath("$.message").value("Customer not found"))
                                .andExpect(jsonPath("$.data").doesNotExist());

                verify(orderService, times(1)).createOrder(any(ApiOrderRequestDto.class));
        }
}
