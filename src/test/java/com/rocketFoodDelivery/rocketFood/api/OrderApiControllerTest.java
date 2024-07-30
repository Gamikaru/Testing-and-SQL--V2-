package com.rocketFoodDelivery.rocketFood.api;

import com.rocketFoodDelivery.rocketFood.controller.api.OrderApiController;
import com.rocketFoodDelivery.rocketFood.dtos.*;
import com.rocketFoodDelivery.rocketFood.exception.ResourceNotFoundException;
import com.rocketFoodDelivery.rocketFood.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class OrderApiControllerTest {

    @InjectMocks
    private OrderApiController orderApiController;

    @Mock
    private OrderService orderService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    // Test change order status
    @Test
    public void testChangeOrderStatus() {
        Integer orderId = 1;
        ApiOrderStatusDto orderStatusDto = ApiOrderStatusDto.builder().status("delivered").build();
        String newStatus = "delivered";

        when(orderService.changeOrderStatus(orderId, orderStatusDto)).thenReturn(newStatus);

        ResponseEntity<?> responseEntity = orderApiController.changeOrderStatus(orderId, orderStatusDto);
        ApiResponseDto responseBody = (ApiResponseDto) responseEntity.getBody();

        assertEquals(200, responseEntity.getStatusCodeValue());
        assertEquals("Success", responseBody.getMessage());
        assertEquals(newStatus, responseBody.getData());

        verify(orderService, times(1)).changeOrderStatus(orderId, orderStatusDto);
    }

    @Test
    public void testChangeOrderStatus_NotFound() {
        Integer orderId = 1;
        ApiOrderStatusDto orderStatusDto = ApiOrderStatusDto.builder().status("delivered").build();

        when(orderService.changeOrderStatus(orderId, orderStatusDto))
                .thenThrow(new ResourceNotFoundException("Order with id " + orderId + " not found"));

        Exception exception = assertThrows(ResourceNotFoundException.class,
                () -> orderApiController.changeOrderStatus(orderId, orderStatusDto));

        assertEquals("Order with id 1 not found", exception.getMessage());

        verify(orderService, times(1)).changeOrderStatus(orderId, orderStatusDto);
    }

    // Test get orders by type and ID
    @Test
    public void testGetOrdersByTypeAndId_Customer() {
        Integer customerId = 1;
        String type = "customer";
        List<ApiOrderDto> orders = Arrays.asList(
                ApiOrderDto.builder().id(1).customer_id(customerId).status("in progress").build(),
                ApiOrderDto.builder().id(2).customer_id(customerId).status("delivered").build());

        when(orderService.getOrdersByTypeAndId(type, customerId)).thenReturn(orders);

        ResponseEntity<?> responseEntity = orderApiController.getOrders(type, customerId);
        ApiResponseDto responseBody = (ApiResponseDto) responseEntity.getBody();

        assertEquals(200, responseEntity.getStatusCodeValue());
        assertEquals("Success", responseBody.getMessage());
        assertEquals(orders, responseBody.getData());

        verify(orderService, times(1)).getOrdersByTypeAndId(type, customerId);
    }

    // Test create order
    @Test
    public void testCreateOrder() {
        ApiOrderRequestDto orderRequestDto = new ApiOrderRequestDto();
        orderRequestDto.setCustomer_id(1);
        orderRequestDto.setRestaurant_id(1);
        orderRequestDto.setProducts(Arrays.asList(
                new ApiProductForOrderApiDto(1, "Product1", 2, 100, 200),
                new ApiProductForOrderApiDto(2, "Product2", 1, 150, 150)));

        ApiOrderDto newOrder = ApiOrderDto.builder()
                .id(1)
                .customer_id(1)
                .restaurant_id(1)
                .status("in progress")
                .total_cost(350)
                .build();

        when(orderService.createOrder(orderRequestDto)).thenReturn(newOrder);

        ResponseEntity<?> responseEntity = orderApiController.createOrder(orderRequestDto);
        ApiResponseDto responseBody = (ApiResponseDto) responseEntity.getBody();

        assertEquals(200, responseEntity.getStatusCodeValue());
        assertEquals("Success", responseBody.getMessage());
        assertEquals(newOrder, responseBody.getData());

        verify(orderService, times(1)).createOrder(orderRequestDto);
    }

    @Test
    public void testCreateOrder_Exception() {
        ApiOrderRequestDto orderRequestDto = new ApiOrderRequestDto();
        orderRequestDto.setCustomer_id(1);
        orderRequestDto.setRestaurant_id(1);
        orderRequestDto.setProducts(Arrays.asList(
                new ApiProductForOrderApiDto(1, "Product1", 2, 100, 200),
                new ApiProductForOrderApiDto(2, "Product2", 1, 150, 150)));

        when(orderService.createOrder(orderRequestDto)).thenThrow(new RuntimeException("Internal server error"));

        ResponseEntity<?> responseEntity = orderApiController.createOrder(orderRequestDto);
        ApiResponseDto responseBody = (ApiResponseDto) responseEntity.getBody();

        assertEquals(500, responseEntity.getStatusCodeValue());
        assertEquals("Internal server error", responseBody.getMessage());
        assertEquals(null, responseBody.getData());

        verify(orderService, times(1)).createOrder(orderRequestDto);
    }
}
