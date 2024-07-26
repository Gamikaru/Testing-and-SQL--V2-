package com.rocketFoodDelivery.rocketFood.api;

import com.rocketFoodDelivery.rocketFood.dtos.AuthRequestDTO;
import com.rocketFoodDelivery.rocketFood.models.UserEntity;
import com.rocketFoodDelivery.rocketFood.repository.UserRepository;
import com.rocketFoodDelivery.rocketFood.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
@Transactional
public class AuthControllerTest {

    private static final Logger logger = LoggerFactory.getLogger(AuthControllerTest.class);

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @MockBean
    private AuthenticationManager authManager;

    @MockBean
    private JwtUtil jwtUtil;

    private List<UserEntity> users;

    @BeforeEach
    public void setUp() {
        logger.info("Setting up test data...");

        // Clean up the database before each test
        userRepository.deleteAll();

        // Create and save multiple users
        users = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            UserEntity user = UserEntity.builder()
                    .email("user" + i + "@example.com")
                    .password("password" + i) // In a real scenario, passwords should be hashed
                    .build();
            userRepository.save(user);
            users.add(user);
            logger.info("Saved user: {}", user.getEmail());
        }
    }

    /**
     * Select a random user from the list of users.
     *
     * @return A random UserEntity.
     */
    private UserEntity getRandomUser() {
        Random rand = new Random();
        UserEntity user = users.get(rand.nextInt(users.size()));
        logger.info("Selected random user: {}", user.getEmail());
        return user;
    }

    @Test
    public void testAuthenticateSuccess() throws Exception {
        // Select a random user
        UserEntity user = getRandomUser();

        // Create authentication request
        AuthRequestDTO request = new AuthRequestDTO();
        request.setEmail(user.getEmail());
        request.setPassword(user.getPassword());

        // Mock authentication manager and JWT utility
        Authentication authentication = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
        Mockito.when(authManager.authenticate(Mockito.any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        Mockito.when(jwtUtil.generateAccessToken(user)).thenReturn("fake-token");

        logger.info("Sending authentication request for user: {}", user.getEmail());

        // Perform authentication request and verify response
        mockMvc.perform(post("/api/auth")
                .contentType("application/json")
                .content("{\"email\": \"" + user.getEmail() + "\", \"password\": \"" + user.getPassword() + "\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.accessToken").value("fake-token"));

        logger.info("Authentication successful for user: {}", user.getEmail());

        // Verify interactions with mocks
        verify(authManager, times(1)).authenticate(Mockito.any(UsernamePasswordAuthenticationToken.class));
        verify(jwtUtil, times(1)).generateAccessToken(user);
    }

    @Test
    public void testAuthenticateFailure() throws Exception {
        // Create authentication request with invalid credentials
        AuthRequestDTO request = new AuthRequestDTO();
        request.setEmail("wrong.email@example.com");
        request.setPassword("wrongpassword");

        // Mock authentication manager to throw BadCredentialsException
        Mockito.when(authManager.authenticate(Mockito.any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        logger.info("Sending authentication request for invalid user");

        // Perform authentication request and verify response
        mockMvc.perform(post("/api/auth")
                .contentType("application/json")
                .content("{\"email\": \"wrong.email@example.com\", \"password\": \"wrongpassword\"}"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false));

        logger.info("Authentication failed for invalid user");

        // Verify interactions with mocks
        verify(authManager, times(1)).authenticate(Mockito.any(UsernamePasswordAuthenticationToken.class));
    }

    // Additional tests for edge cases and validation errors
    @Test
    public void testAuthenticateWithMissingEmail() throws Exception {
        // Create authentication request with missing email
        AuthRequestDTO request = new AuthRequestDTO();
        request.setPassword("password");

        // Perform authentication request and verify response
        mockMvc.perform(post("/api/auth")
                .contentType("application/json")
                .content("{\"password\": \"password\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testAuthenticateWithMissingPassword() throws Exception {
        // Create authentication request with missing password
        AuthRequestDTO request = new AuthRequestDTO();
        request.setEmail("user@example.com");

        // Perform authentication request and verify response
        mockMvc.perform(post("/api/auth")
                .contentType("application/json")
                .content("{\"email\": \"user@example.com\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testAuthenticateWithInvalidEmailFormat() throws Exception {
        // Create authentication request with invalid email format
        AuthRequestDTO request = new AuthRequestDTO();
        request.setEmail("invalid-email");
        request.setPassword("password");

        // Perform authentication request and verify response
        mockMvc.perform(post("/api/auth")
                .contentType("application/json")
                .content("{\"email\": \"invalid-email\", \"password\": \"password\"}"))
                .andExpect(status().isBadRequest());
    }
}
