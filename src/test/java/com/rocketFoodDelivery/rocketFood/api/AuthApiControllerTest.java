package com.rocketFoodDelivery.rocketFood.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rocketFoodDelivery.rocketFood.RocketFoodApplication;
import com.rocketFoodDelivery.rocketFood.dtos.AuthRequestDto;
import com.rocketFoodDelivery.rocketFood.models.UserEntity;
import com.rocketFoodDelivery.rocketFood.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration tests for the AuthApiController.
 */
@SpringBootTest(classes = RocketFoodApplication.class)
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
public class AuthApiControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @MockBean
        private AuthenticationManager authManager;

        @MockBean
        private JwtUtil jwtUtil;

        @Autowired
        private ObjectMapper objectMapper;

        private static final String BASE_URI = "/api/auth";

        private AuthRequestDto validAuthRequest;
        private UserEntity validUser;

        /**
         * Setup method to initialize test data before each test.
         */
        @BeforeEach
        public void setUp() {
                validAuthRequest = createValidAuthRequest();
                validUser = createValidUser();
        }

        private AuthRequestDto createValidAuthRequest() {
                return AuthRequestDto.builder()
                                .email("test@user.com")
                                .password("password")
                                .build();
        }

        private UserEntity createValidUser() {
                return UserEntity.builder()
                                .email("test@user.com")
                                .password("password")
                                .build();
        }

        /**
         * Tests successful authentication with valid credentials.
         * 
         * @throws Exception if an error occurs during the test.
         */
        @Test
        public void testAuthenticateWithValidCredentials() throws Exception {
                Authentication authentication = new UsernamePasswordAuthenticationToken(validUser, null);
                Mockito.when(authManager.authenticate(Mockito.any(UsernamePasswordAuthenticationToken.class)))
                                .thenReturn(authentication);
                Mockito.when(jwtUtil.generateAccessToken(validUser)).thenReturn("dummy-jwt-token");

                mockMvc.perform(post(BASE_URI)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(validAuthRequest)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.success").value(true))
                                .andExpect(jsonPath("$.accessToken").value("dummy-jwt-token"));
        }

        /**
         * Tests authentication with invalid credentials.
         * 
         * @throws Exception if an error occurs during the test.
         */
        @Test
        public void testAuthenticateWithInvalidCredentials() throws Exception {
                Mockito.when(authManager.authenticate(Mockito.any(UsernamePasswordAuthenticationToken.class)))
                                .thenThrow(new BadCredentialsException("Invalid credentials"));

                mockMvc.perform(post(BASE_URI)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(validAuthRequest)))
                                .andExpect(status().isUnauthorized())
                                .andExpect(jsonPath("$.success").value(false))
                                .andExpect(jsonPath("$.message").value("Invalid email or password"));
        }

        /**
         * Tests authentication with an unexpected error.
         * 
         * @throws Exception if an error occurs during the test.
         */
        @Test
        public void testAuthenticateWithUnexpectedError() throws Exception {
                // Simulate a general runtime exception to test unexpected error handling
                Mockito.when(authManager.authenticate(Mockito.any(UsernamePasswordAuthenticationToken.class)))
                                .thenThrow(new RuntimeException("Unexpected error"));

                mockMvc.perform(post(BASE_URI)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(validAuthRequest)))
                                .andExpect(status().isInternalServerError()) // Expect 500 Internal Server Error
                                .andExpect(jsonPath("$.success").value(false))
                                .andExpect(jsonPath("$.message").value("Unexpected error occurred"));
        }

        /**
         * Tests authentication with a missing email field.
         * 
         * @throws Exception if an error occurs during the test.
         */
        @Test
        public void testAuthenticateWithMissingEmail() throws Exception {
                AuthRequestDto invalidAuthRequest = AuthRequestDto.builder()
                                .password("password")
                                .build();

                mockMvc.perform(post(BASE_URI)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(invalidAuthRequest)))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.success").value(false))
                                .andExpect(jsonPath("$.message").value("Email is mandatory"));
        }

        /**
         * Tests authentication with a missing password field.
         * 
         * @throws Exception if an error occurs during the test.
         */
        @Test
        public void testAuthenticateWithMissingPassword() throws Exception {
                AuthRequestDto invalidAuthRequest = AuthRequestDto.builder()
                                .email("test@user.com")
                                .build();

                mockMvc.perform(post(BASE_URI)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(invalidAuthRequest)))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.success").value(false))
                                .andExpect(jsonPath("$.message").value("Password is mandatory"));
        }
}
