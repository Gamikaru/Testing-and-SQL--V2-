// package com.rocketFoodDelivery.rocketFood.api;

// import com.rocketFoodDelivery.rocketFood.controller.api.ProductApiController;
// import com.rocketFoodDelivery.rocketFood.dtos.ApiProductDto;
// import com.rocketFoodDelivery.rocketFood.dtos.ApiResponseDto;
// import com.rocketFoodDelivery.rocketFood.service.ProductService;
// import com.rocketFoodDelivery.rocketFood.util.ResponseBuilder;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.mockito.InjectMocks;
// import org.mockito.Mock;
// import org.mockito.MockitoAnnotations;
// import org.springframework.http.ResponseEntity;

// import java.util.Collections;
// import java.util.List;

// import static org.junit.jupiter.api.Assertions.assertEquals;
// import static org.mockito.Mockito.*;

// public class ProductApiControllerTest {

//     @InjectMocks
//     private ProductApiController productApiController;

//     @Mock
//     private ProductService productService;

//     @BeforeEach
//     public void setup() {
//         MockitoAnnotations.openMocks(this);
//     }

//     @Test
//     public void testGetProducts() {
//         Integer restaurantId = 1;
//         ApiProductDto productDto = ApiProductDto.builder().id(1).name("Cheeseburger").cost(525).build();
//         List<ApiProductDto> products = Collections.singletonList(productDto);

//         when(productService.getProductsByRestaurantId(restaurantId)).thenReturn(products);

//         ResponseEntity<?> responseEntity = productApiController.getProducts(restaurantId);
//         assertEquals(200, responseEntity.getStatusCodeValue());

//         ApiResponseDto responseBody = (ApiResponseDto) responseEntity.getBody();
//         assertEquals("Success", responseBody.getMessage());
//         assertEquals(products, responseBody.getData());

//         verify(productService, times(1)).getProductsByRestaurantId(restaurantId);
//     }

//     @Test
//     public void testGetProducts_NoProductsFound() {
//         Integer restaurantId = 1;

//         when(productService.getProductsByRestaurantId(restaurantId)).thenReturn(Collections.emptyList());

//         ResponseEntity<?> responseEntity = productApiController.getProducts(restaurantId);
//         assertEquals(200, responseEntity.getStatusCodeValue());

//         ApiResponseDto responseBody = (ApiResponseDto) responseEntity.getBody();
//         assertEquals("Success", responseBody.getMessage());
//         assertEquals(Collections.emptyList(), responseBody.getData());

//         verify(productService, times(1)).getProductsByRestaurantId(restaurantId);
//     }

//     @Test
//     public void testGetProducts_InternalServerError() {
//         Integer restaurantId = 1;

//         when(productService.getProductsByRestaurantId(restaurantId))
//                 .thenThrow(new RuntimeException("Internal server error"));

//         ResponseEntity<?> responseEntity = productApiController.getProducts(restaurantId);
//         assertEquals(500, responseEntity.getStatusCodeValue());

//         ApiResponseDto responseBody = (ApiResponseDto) responseEntity.getBody();
//         assertEquals("Internal server error", responseBody.getMessage());
//         assertEquals(null, responseBody.getData());

//         verify(productService, times(1)).getProductsByRestaurantId(restaurantId);
//     }

//     @Test
//     public void testGetProducts_InvalidRestaurantId() {
//         Integer invalidRestaurantId = -1;

//         when(productService.getProductsByRestaurantId(invalidRestaurantId)).thenReturn(Collections.emptyList());

//         ResponseEntity<?> responseEntity = productApiController.getProducts(invalidRestaurantId);
//         assertEquals(200, responseEntity.getStatusCodeValue());

//         ApiResponseDto responseBody = (ApiResponseDto) responseEntity.getBody();
//         assertEquals("Success", responseBody.getMessage());
//         assertEquals(Collections.emptyList(), responseBody.getData());

//         verify(productService, times(1)).getProductsByRestaurantId(invalidRestaurantId);
//     }
// }
