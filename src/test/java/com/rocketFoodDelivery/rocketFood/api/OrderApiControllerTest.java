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

// Define the Spring Boot test class for OrderApiController
@SpringBootTest(classes = RocketFoodApplication.class)
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
public class OrderApiControllerTest {

        // Define constants for reuse
        private static final String BASE_URL = "/api/orders";
        private static final String SUCCESS_MESSAGE = "Success";
        private static final String NOT_FOUND_MESSAGE = "Order with id 1 not found";

        @Autowired
        private MockMvc mockMvc; // MockMvc to perform HTTP requests in tests

        @MockBean
        private OrderService orderService; // Mock OrderService bean

        @Autowired
        private ObjectMapper objectMapper; // ObjectMapper for JSON serialization/deserialization

        @InjectMocks
        private OrderApiController orderApiController; // Inject mock OrderApiController

        private ApiOrderRequestDto orderRequestDto; // DTO for order request
        private ApiOrderDto newOrder; // DTO for new order
        private ApiOrderStatusDto orderStatusDto; // DTO for order status
        private Integer orderId; // Order ID

        // Initialize mocks and setup test data before each test
        @BeforeEach
        public void setup() {
                MockitoAnnotations.openMocks(this);
                setupTestData();
        }

        // Setup test data for use in the tests
        private void setupTestData() {
                orderId = 1;

                // Setup order request DTO
                orderRequestDto = new ApiOrderRequestDto();
                orderRequestDto.setCustomer_id(1);
                orderRequestDto.setRestaurant_id(1);
                orderRequestDto.setProducts(Arrays.asList(
                                new ApiProductOrderRequestDto(1, 2),
                                new ApiProductOrderRequestDto(2, 1)));

                // Setup new order DTO
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

                // Setup order status DTO
                orderStatusDto = ApiOrderStatusDto.builder().status("delivered").build();
        }

        // Test changing the order status successfully
        @Test
        @WithMockUser
        public void testChangeOrderStatus() throws Exception {
                // Mock the behavior of orderService
                when(orderService.changeOrderStatus(orderId, orderStatusDto)).thenReturn(orderStatusDto.getStatus());

                // Perform POST request to change order status and verify the response
                mockMvc.perform(post(BASE_URL + "/{order_id}/status", orderId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(orderStatusDto)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.message").value(SUCCESS_MESSAGE))
                                .andExpect(jsonPath("$.data.status").value(orderStatusDto.getStatus()));

                // Verify the interaction with orderService
                verify(orderService, times(1)).changeOrderStatus(orderId, orderStatusDto);
        }

        // Test changing the order status when order is not found
        @Test
        @WithMockUser
        public void testChangeOrderStatus_NotFound() throws Exception {
                // Mock the behavior of orderService to throw exception
                when(orderService.changeOrderStatus(orderId, orderStatusDto))
                                .thenThrow(new ResourceNotFoundException(NOT_FOUND_MESSAGE));

                // Perform POST request to change order status and verify the response
                mockMvc.perform(post(BASE_URL + "/{order_id}/status", orderId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(orderStatusDto)))
                                .andExpect(status().isNotFound())
                                .andExpect(jsonPath("$.message").value(NOT_FOUND_MESSAGE))
                                .andExpect(jsonPath("$.data").doesNotExist());

                // Verify the interaction with orderService
                verify(orderService, times(1)).changeOrderStatus(orderId, orderStatusDto);
        }

        // Test fetching orders by type and ID for customer
        @Test
        @WithMockUser
        public void testGetOrdersByTypeAndId_Customer() throws Exception {
                // Setup mock orders list
                List<ApiOrderDto> orders = Arrays.asList(
                                ApiOrderDto.builder().id(1).customer_id(1).status("in progress").build(),
                                ApiOrderDto.builder().id(2).customer_id(1).status("delivered").build());

                // Mock the behavior of orderService
                when(orderService.getOrdersByTypeAndId("customer", 1)).thenReturn(orders);

                // Perform GET request to fetch orders and verify the response
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

                // Verify the interaction with orderService
                verify(orderService, times(1)).getOrdersByTypeAndId("customer", 1);
        }

        // Test creating an order successfully
        @Test
        @WithMockUser
        public void testCreateOrder() throws Exception {
                // Mock the behavior of orderService
                when(orderService.createOrder(any(ApiOrderRequestDto.class))).thenReturn(newOrder);

                // Perform POST request to create order and verify the response
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

                // Verify the interaction with orderService
                verify(orderService, times(1)).createOrder(any(ApiOrderRequestDto.class));
        }

        // Test creating an order when customer is not found
        @Test
        @WithMockUser
        public void testCreateOrder_Exception() throws Exception {
                // Mock the behavior of orderService to throw exception
                when(orderService.createOrder(any(ApiOrderRequestDto.class)))
                                .thenThrow(new ResourceNotFoundException("Customer not found"));

                // Perform POST request to create order and verify the response
                mockMvc.perform(post(BASE_URL)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(orderRequestDto)))
                                .andExpect(status().isNotFound())
                                .andExpect(jsonPath("$.message").value("Customer not found"))
                                .andExpect(jsonPath("$.data").doesNotExist());

                // Verify the interaction with orderService
                verify(orderService, times(1)).createOrder(any(ApiOrderRequestDto.class));
        }
}
