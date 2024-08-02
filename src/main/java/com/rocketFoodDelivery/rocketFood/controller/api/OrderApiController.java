package com.rocketFoodDelivery.rocketFood.controller.api;

import com.rocketFoodDelivery.rocketFood.dtos.ApiOrderRequestDto;
import com.rocketFoodDelivery.rocketFood.dtos.ApiOrderStatusDto;
import com.rocketFoodDelivery.rocketFood.dtos.ApiOrderDto;
import com.rocketFoodDelivery.rocketFood.service.OrderService;
import com.rocketFoodDelivery.rocketFood.util.ResponseBuilder;
import com.rocketFoodDelivery.rocketFood.exception.ResourceNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.http.HttpStatus;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/orders")
public class OrderApiController {

    private final OrderService orderService;

    public OrderApiController(OrderService orderService) {
        this.orderService = orderService;
    }

    /**
     * Change the status of an order.
     * 
     * @param orderId        The ID of the order to change.
     * @param orderStatusDto The new status of the order.
     * @return ResponseEntity with the result of the operation.
     */
    @PostMapping("/{order_id}/status")
    public ResponseEntity<?> changeOrderStatus(@PathVariable("order_id") Integer orderId,
            @RequestBody ApiOrderStatusDto orderStatusDto) {
        log.info("Changing status of order ID: {} to {}", orderId, orderStatusDto.getStatus());

        try {
            validateOrderStatus(orderStatusDto);

            String newStatus = orderService.changeOrderStatus(orderId, orderStatusDto);
            log.info("Order status changed successfully: {}", newStatus);

            ApiOrderStatusDto responseDto = ApiOrderStatusDto.builder().status(newStatus).build();
            // Changed to use HttpStatus instead of integer
            return ResponseBuilder.buildResponse("Success", responseDto, HttpStatus.OK);
        } catch (ResourceNotFoundException ex) {
            log.error("Order not found with ID: {}", orderId);
            return ResponseBuilder.buildNotFoundResponse("Order with id " + orderId + " not found");
        } catch (IllegalArgumentException ex) {
            log.error("Invalid status parameter: {}", ex.getMessage());
            return ResponseBuilder.buildBadRequestResponse("Invalid or missing parameters");
        } catch (Exception ex) {
            log.error("Exception occurred while changing order status: {}", ex.getMessage(), ex);
            // Changed to use HttpStatus instead of integer
            return ResponseBuilder.buildErrorResponse("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Fetch orders based on type and ID.
     * 
     * @param type The type of orders to fetch.
     * @param id   The ID associated with the type.
     * @return ResponseEntity with the list of orders.
     */
    @GetMapping
    public ResponseEntity<?> getOrders(@RequestParam String type, @RequestParam Integer id) {
        log.info("Fetching orders for type: {} with ID: {}", type, id);

        try {
            List<ApiOrderDto> orders = orderService.getOrdersByTypeAndId(type, id);
            log.info("Fetched orders: {}", orders);
            // Changed to use HttpStatus instead of integer
            return ResponseBuilder.buildResponse("Success", orders, HttpStatus.OK);
        } catch (IllegalArgumentException ex) {
            log.error("Invalid type provided: {}", ex.getMessage(), ex);
            return ResponseBuilder.buildBadRequestResponse("Invalid or missing parameters");
        } catch (Exception ex) {
            log.error("Exception occurred while fetching orders: {}", ex.getMessage(), ex);
            // Changed to use HttpStatus instead of integer
            return ResponseBuilder.buildErrorResponse("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Create a new order.
     * 
     * @param orderRequestDto The order details.
     * @return ResponseEntity with the created order details.
     */
    @PostMapping
    public ResponseEntity<?> createOrder(@RequestBody ApiOrderRequestDto orderRequestDto) {
        log.info("Received order creation request: {}", orderRequestDto);

        try {
            ApiOrderDto orderDto = orderService.createOrder(orderRequestDto);
            log.debug("Order created: {}", orderDto);
            // Changed to use HttpStatus instead of integer
            return ResponseBuilder.buildResponse("Success", orderDto, HttpStatus.CREATED);
        } catch (ResourceNotFoundException ex) {
            log.error("Resource not found: {}", ex.getMessage());
            return ResponseBuilder.buildNotFoundResponse(ex.getMessage());
        } catch (Exception ex) {
            log.error("Unexpected error: {}", ex.getMessage());
            // Changed to use HttpStatus instead of integer
            return ResponseBuilder.buildErrorResponse("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Validate the order status DTO.
     * 
     * @param orderStatusDto The order status DTO to validate.
     * @throws IllegalArgumentException if status is invalid.
     */
    private void validateOrderStatus(ApiOrderStatusDto orderStatusDto) {
        if (orderStatusDto.getStatus() == null || orderStatusDto.getStatus().isEmpty()) {
            log.error("Invalid or missing parameters");
            throw new IllegalArgumentException("Invalid or missing parameters");
        }
    }
}
