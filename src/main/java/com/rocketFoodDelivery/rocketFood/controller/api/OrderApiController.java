package com.rocketFoodDelivery.rocketFood.controller.api;

import com.rocketFoodDelivery.rocketFood.dtos.ApiOrderStatusDto;
import com.rocketFoodDelivery.rocketFood.service.OrderService;
import com.rocketFoodDelivery.rocketFood.util.ResponseBuilder;
import com.rocketFoodDelivery.rocketFood.dtos.ApiOrderDto;
import com.rocketFoodDelivery.rocketFood.dtos.ApiOrderRequestDto;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/order")
public class OrderApiController {

    private final OrderService orderService;

    public OrderApiController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/{order_id}/status")
    public ResponseEntity<?> changeOrderStatus(@PathVariable("order_id") Integer orderId,
            @RequestBody ApiOrderStatusDto orderStatusDto) {
        log.info("Changing status of order ID: {} to {}", orderId, orderStatusDto.getStatus());

        String newStatus = orderService.changeOrderStatus(orderId, orderStatusDto);
        log.info("Order status changed successfully: {}", newStatus);
        return ResponseBuilder.buildResponse("Success", newStatus, 200);
    }

    @GetMapping
    public ResponseEntity<?> getOrders(@RequestParam String type, @RequestParam Integer id) {
        log.info("Fetching orders for type: {} with ID: {}", type, id);

        try {
            List<ApiOrderDto> orders = orderService.getOrdersByTypeAndId(type, id);
            log.info("Fetched orders: {}", orders);
            return ResponseBuilder.buildResponse("Success", orders, 200);
        } catch (IllegalArgumentException ex) {
            log.error("Invalid type provided: {}", ex.getMessage());
            throw ex; // Ensure this exception is thrown to be caught by the GlobalExceptionHandler
        } catch (Exception ex) {
            log.error("Exception occurred while fetching orders: {}", ex.getMessage());
            return ResponseBuilder.buildErrorResponse("Internal server error", 500);
        }
    }

    @PostMapping
    public ResponseEntity<?> createOrder(@RequestBody ApiOrderRequestDto orderRequestDto) {
        log.info("Creating a new order");

        try {
            ApiOrderDto newOrder = orderService.createOrder(orderRequestDto);
            log.info("Order created successfully: {}", newOrder);
            return ResponseBuilder.buildResponse("Success", newOrder, 200);
        } catch (Exception ex) {
            log.error("Exception occurred while creating order: {}", ex.getMessage());
            return ResponseBuilder.buildErrorResponse("Internal server error", 500);
        }
    }
}
