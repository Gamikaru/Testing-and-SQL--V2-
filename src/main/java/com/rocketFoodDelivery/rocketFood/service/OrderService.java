package com.rocketFoodDelivery.rocketFood.service;

import com.rocketFoodDelivery.rocketFood.dtos.ApiOrderDto;
import com.rocketFoodDelivery.rocketFood.dtos.ApiOrderRequestDto;
import com.rocketFoodDelivery.rocketFood.dtos.ApiOrderStatusDto;
import com.rocketFoodDelivery.rocketFood.dtos.ApiProductForOrderApiDto;
import com.rocketFoodDelivery.rocketFood.exception.ResourceNotFoundException;
import com.rocketFoodDelivery.rocketFood.models.*;
import com.rocketFoodDelivery.rocketFood.repository.OrderRepository;
import com.rocketFoodDelivery.rocketFood.repository.OrderStatusRepository;
import com.rocketFoodDelivery.rocketFood.repository.ProductOrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderStatusRepository orderStatusRepository;
    private final ProductOrderRepository productOrderRepository;

    public OrderService(OrderRepository orderRepository, OrderStatusRepository orderStatusRepository,
            ProductOrderRepository productOrderRepository) {
        this.orderRepository = orderRepository;
        this.orderStatusRepository = orderStatusRepository;
        this.productOrderRepository = productOrderRepository;
    }

    public String changeOrderStatus(Integer orderId, ApiOrderStatusDto orderStatusDto) {
        log.info("Fetching order by ID: {}", orderId);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order with id " + orderId + " not found"));

        OrderStatus orderStatus = orderStatusRepository.findByName(orderStatusDto.getStatus())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Order status " + orderStatusDto.getStatus() + " not found"));

        log.info("Updating order status to: {}", orderStatusDto.getStatus());
        order.setOrder_status(orderStatus);
        orderRepository.save(order);

        return order.getOrder_status().getName();
    }

    public List<ApiOrderDto> getOrdersByTypeAndId(String type, Integer id) {
        log.info("Fetching orders by type: {} and ID: {}", type, id);
        List<Order> orders;
        switch (type.toLowerCase()) {
            case "customer":
                log.info("Fetching orders for customer ID: {}", id);
                orders = orderRepository.findByCustomerId(id);
                break;
            case "restaurant":
                log.info("Fetching orders for restaurant ID: {}", id);
                orders = orderRepository.findByRestaurantId(id);
                break;
            case "courier":
                log.info("Fetching orders for courier ID: {}", id);
                orders = orderRepository.findByCourierId(id);
                break;
            default:
                log.error("Invalid type: {}", type);
                throw new IllegalArgumentException("Invalid type: " + type);
        }
        log.info("Orders fetched: {}", orders);
        return orders.stream().map(this::mapToApiOrderDto).collect(Collectors.toList());
    }

    public ApiOrderDto createOrder(ApiOrderRequestDto orderRequestDto) {
        Order order = new Order();
        // Assume setters for setting customer, restaurant, and products are available
        order.setCustomer(new Customer(orderRequestDto.getCustomer_id()));
        order.setRestaurant(new Restaurant(orderRequestDto.getRestaurant_id()));
        order.setOrder_status(orderStatusRepository.findByName("in progress").orElseThrow(
                () -> new ResourceNotFoundException("Order status 'in progress' not found")));

        orderRepository.save(order);

        List<ProductOrder> productOrders = orderRequestDto.getProducts().stream().map(productRequestDto -> {
            ProductOrder productOrder = new ProductOrder();
            productOrder.setOrder(order);
            productOrder.setProduct(new Product(productRequestDto.getId()));
            productOrder.setProduct_quantity(productRequestDto.getQuantity());
            productOrder.setProduct_unit_cost(productRequestDto.getUnit_cost());
            return productOrder;
        }).collect(Collectors.toList());

        productOrderRepository.saveAll(productOrders);

        return mapToApiOrderDto(order);
    }

    private ApiOrderDto mapToApiOrderDto(Order order) {
        List<ApiProductForOrderApiDto> products = productOrderRepository.findByOrderId(order.getId()).stream()
                .map(productOrder -> new ApiProductForOrderApiDto(
                        productOrder.getProduct().getId(),
                        productOrder.getProduct().getName(),
                        productOrder.getProduct_quantity(),
                        productOrder.getProduct_unit_cost(),
                        productOrder.getProduct_quantity() * productOrder.getProduct_unit_cost()))
                .collect(Collectors.toList());

        return ApiOrderDto.builder()
                .id(order.getId())
                .customer_id(order.getCustomer().getId())
                .customer_name(order.getCustomer().getUserEntity().getName())
                .customer_address(order.getCustomer().getAddress().toString())
                .restaurant_id(order.getRestaurant().getId())
                .restaurant_name(order.getRestaurant().getName())
                .restaurant_address(order.getRestaurant().getAddress().toString())
                .courier_id(order.getCourier() != null ? order.getCourier().getId() : null)
                .courier_name(order.getCourier() != null ? order.getCourier().getUserEntity().getName() : null)
                .status(order.getOrder_status().getName())
                .products(products)
                .total_cost(products.stream().mapToInt(ApiProductForOrderApiDto::getTotal_cost).sum())
                .build();
    }
}
