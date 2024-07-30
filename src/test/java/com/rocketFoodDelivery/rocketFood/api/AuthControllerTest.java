// package com.rocketFoodDelivery.rocketFood.api;

// import com.fasterxml.jackson.databind.ObjectMapper;
// import com.rocketFoodDelivery.rocketFood.RocketFoodApplication;
// import com.rocketFoodDelivery.rocketFood.dtos.AuthRequestDto;
// import com.rocketFoodDelivery.rocketFood.models.UserEntity;
// import com.rocketFoodDelivery.rocketFood.security.JwtUtil;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;

// import org.mockito.Mockito;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
// import org.springframework.boot.test.context.SpringBootTest;
// import org.springframework.boot.test.mock.mockito.MockBean;
// import org.springframework.http.MediaType;
// import org.springframework.security.authentication.AuthenticationManager;
// import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
// import org.springframework.security.core.Authentication;
// import org.springframework.security.core.userdetails.UsernameNotFoundException;
// import org.springframework.test.context.TestPropertySource;
// import org.springframework.test.web.servlet.MockMvc;

// import static org.mockito.ArgumentMatchers.any;
// import static org.mockito.Mockito.when;
// import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
// import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
// import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
// import org.springframework.test.web.servlet.ResultActions;


// @SpringBootTest(classes = RocketFoodApplication.class)
// @AutoConfigureMockMvc
// @TestPropertySource(locations = "classpath:application-test.properties")
// public class AuthControllerTest {

//     @Autowired
//     private MockMvc mockMvc;

//     @Autowired
//     private ObjectMapper objectMapper;

//     @MockBean
//     private AuthenticationManager authManager;

//     @MockBean
//     private JwtUtil jwtUtil;

//     private AuthRequestDto validAuthRequest;
//     private UserEntity validUser;
//     private Authentication authentication;

//     @BeforeEach
//     public void setUp() {
//         validAuthRequest = new AuthRequestDto("test@user.com", "password");
//         validUser = UserEntity.builder()
//                 .id(1)
//                 .email("test@user.com")
//                 .password("password")
//                 .build();

//         authentication = new UsernamePasswordAuthenticationToken(validUser, null, validUser.getAuthorities());
//     }

//     @Test
//     public void testAuthenticateWithValidData() throws Exception {
//         setUpMocksForValidData();

//         performPostRequest(validAuthRequest)
//                 .andExpect(status().isOk())
//                 .andExpect(jsonPath("$.success").value(true))
//                 .andExpect(jsonPath("$.accessToken").value("valid-token"))
//                 .andExpect(jsonPath("$.userId").value(validUser.getId()))
//                 .andExpect(jsonPath("$.customerId").value(0))
//                 .andExpect(jsonPath("$.courierId").value(0));
//     }

//     @Test
//     public void testAuthenticateWithInvalidData() throws Exception {
//         when(authManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
//                 .thenThrow(new UsernameNotFoundException("User not found"));

//         performPostRequest(validAuthRequest)
//                 .andExpect(status().isUnauthorized())
//                 .andExpect(jsonPath("$.success").value(false))
//                 .andExpect(jsonPath("$.message").value("Authentication failed. Please check your credentials."));
//     }

//     @Test
//     public void testAuthenticateWithBadRequest() throws Exception {
//         AuthRequestDto invalidAuthRequest = new AuthRequestDto("", "");

//         performPostRequest(invalidAuthRequest)
//                 .andExpect(status().isBadRequest())
//                 .andExpect(jsonPath("$.success").value(false))
//                 .andExpect(jsonPath("$.message").value("Invalid or missing parameters"));
//     }

//     private void setUpMocksForValidData() {
//         when(authManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
//                 .thenReturn(authentication);
//         when(jwtUtil.generateAccessToken(validUser)).thenReturn("valid-token");
//     }

//     private ResultActions performPostRequest(AuthRequestDto authRequestDto) throws Exception {
//         return mockMvc.perform(post("/api/auth/login")
//                 .contentType(MediaType.APPLICATION_JSON)
//                 .content(objectMapper.writeValueAsString(authRequestDto)));
//     }
// }
