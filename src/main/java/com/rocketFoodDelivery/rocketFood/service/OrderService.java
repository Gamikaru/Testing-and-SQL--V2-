package com.rocketFoodDelivery.rocketFood.service;

import com.rocketFoodDelivery.rocketFood.dtos.*;
import com.rocketFoodDelivery.rocketFood.exception.ResourceNotFoundException;
import com.rocketFoodDelivery.rocketFood.models.*;
import com.rocketFoodDelivery.rocketFood.repository.*;
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
        private final ProductRepository productRepository;
        private final RestaurantRepository restaurantRepository;
        private final CustomerRepository customerRepository;

        /**
         * Constructor for dependency injection.
         */
        public OrderService(OrderRepository orderRepository, OrderStatusRepository orderStatusRepository,
                        ProductOrderRepository productOrderRepository, ProductRepository productRepository,
                        RestaurantRepository restaurantRepository, CustomerRepository customerRepository) {
                this.orderRepository = orderRepository;
                this.orderStatusRepository = orderStatusRepository;
                this.productOrderRepository = productOrderRepository;
                this.productRepository = productRepository;
                this.restaurantRepository = restaurantRepository;
                this.customerRepository = customerRepository;
        }

        /**
         * Changes the status of an order.
         *
         * @param orderId        The ID of the order.
         * @param orderStatusDto The new status of the order.
         * @return The updated order status.
         */
        public String changeOrderStatus(Integer orderId, ApiOrderStatusDto orderStatusDto) {
                log.info("Fetching order by ID: {}", orderId);

                Order order = orderRepository.findById(orderId)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Order with id " + orderId + " not found"));

                OrderStatus orderStatus = orderStatusRepository.findByName(orderStatusDto.getStatus())
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Order status " + orderStatusDto.getStatus() + " not found"));

                log.info("Updating order status to: {}", orderStatusDto.getStatus());

                order.setOrder_status(orderStatus);
                orderRepository.save(order);

                return order.getOrder_status().getName();
        }

        /**
         * Retrieves orders based on type and ID.
         *
         * @param type The type of entity (customer, restaurant, courier).
         * @param id   The ID of the entity.
         * @return A list of orders.
         */
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

        /**
         * Creates a new order.
         *
         * @param orderRequestDto The order request data transfer object.
         * @return The created order data transfer object.
         */
        public ApiOrderDto createOrder(ApiOrderRequestDto orderRequestDto) {
                log.info("Creating order for customer ID: {}, restaurant ID: {}", orderRequestDto.getCustomer_id(),
                                orderRequestDto.getRestaurant_id());

                // Validate if the restaurant exists
                log.debug("Fetching restaurant with ID: {}", orderRequestDto.getRestaurant_id());
                Restaurant restaurant = restaurantRepository.findById(orderRequestDto.getRestaurant_id())
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Restaurant with id " + orderRequestDto.getRestaurant_id()
                                                                + " not found"));

                // Validate if the customer exists
                log.debug("Fetching customer with ID: {}", orderRequestDto.getCustomer_id());
                Customer customer = customerRepository.findById(orderRequestDto.getCustomer_id())
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Customer with id " + orderRequestDto.getCustomer_id() + " not found"));

                // Log product details
                orderRequestDto.getProducts().forEach(
                                product -> log.info("Product ID: {}, Quantity: {}", product.getId(),
                                                product.getQuantity()));

                // Create and save the order
                Order order = new Order();
                order.setCustomer(customer); // Set the full Customer object
                order.setRestaurant(restaurant); // Set the full Restaurant object
                order.setOrder_status(orderStatusRepository.findByName("in progress")
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Order status 'in progress' not found")));
                order.setRestaurant_rating(1);

                orderRepository.save(order); // Save order after validation

                // Validate if each product exists and belongs to the restaurant
                final Order savedOrder = order;
                List<ProductOrder> productOrders = orderRequestDto.getProducts().stream().map(productRequestDto -> {
                        ProductOrder productOrder = new ProductOrder();
                        productOrder.setOrder(savedOrder);

                        Product product = productRepository.findByIdAndRestaurantId(productRequestDto.getId(),
                                        orderRequestDto.getRestaurant_id())
                                        .orElseThrow(() -> new ResourceNotFoundException(
                                                        "Product with id " + productRequestDto.getId()
                                                                        + " not found or does not belong to the restaurant"));

                        productOrder.setProduct(product);
                        productOrder.setProduct_quantity(productRequestDto.getQuantity());
                        productOrder.setProduct_unit_cost(product.getUnitCost()); // Setting product unit cost from the
                                                                                  // product entity
                        log.info("Added product ID: {} to order", productRequestDto.getId()); // Added log
                        return productOrder;
                }).collect(Collectors.toList());

                productOrderRepository.saveAll(productOrders);
                log.info("Order created successfully with ID: {}", order.getId()); // Added log

                Order finalOrder = orderRepository.findById(order.getId())
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Order with id " + order.getId() + " not found after save")); // Ensure
                                                                                                              // the
                                                                                                              // order
                                                                                                              // is
                                                                                                              // retrieved
                                                                                                              // after
                                                                                                              // save
                return mapToApiOrderDto(finalOrder); // Ensure this method correctly converts Order to ApiOrderDto
        }

        /**
         * Maps an Order entity to an ApiOrderDto.
         *
         * @param order The order entity.
         * @return The API order data transfer object.
         */
        private ApiOrderDto mapToApiOrderDto(Order order) {
                List<ApiProductForOrderApiDto> products = productOrderRepository.findByOrderId(order.getId()).stream()
                                .map(productOrder -> new ApiProductForOrderApiDto(
                                                productOrder.getProduct().getId(),
                                                productOrder.getProduct().getName(),
                                                productOrder.getProduct_quantity(),
                                                productOrder.getProduct_unit_cost(),
                                                productOrder.getProduct_quantity()
                                                                * productOrder.getProduct_unit_cost()))
                                .collect(Collectors.toList());

                return ApiOrderDto.builder()
                                .id(order.getId())
                                .customer_id(order.getCustomer().getId())
                                .restaurant_id(order.getRestaurant().getId())
                                .courier_id(order.getCourier() != null ? order.getCourier().getId() : null)
                                .status(order.getOrder_status().getName())
                                .products(products)
                                .total_cost(products.stream().mapToInt(ApiProductForOrderApiDto::getTotal_cost).sum())
                                .build();
        }
}
