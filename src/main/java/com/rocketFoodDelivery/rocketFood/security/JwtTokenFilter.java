package com.rocketFoodDelivery.rocketFood.security;

import com.rocketFoodDelivery.rocketFood.models.UserEntity;
import com.rocketFoodDelivery.rocketFood.util.ResponseBuilder;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.http.HttpStatus;

import java.io.IOException;

/**
 * Filter to handle JWT token authentication.
 * This filter checks the JWT token in the Authorization header of each request,
 * validates it, and sets the authentication context if the token is valid.
 */
@Component
public class JwtTokenFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * Filters each request and validates the JWT token if present.
     * 
     * @param request     the HttpServletRequest
     * @param response    the HttpServletResponse
     * @param filterChain the FilterChain
     * @throws ServletException in case of a servlet error
     * @throws IOException      in case of an I/O error
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // If the request does not have an Authorization header, skip the filter
        if (!hasAuthorizationBearer(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        // Extract the token from the Authorization header
        String token = getAccessToken(request);

        try {
            // Validate the token
            if (!jwtUtil.validateAccessToken(token)) {
                // If the token is invalid, respond with an error
                ResponseBuilder.buildErrorResponse(response, "Invalid Token", HttpStatus.UNAUTHORIZED);
                return;
            }

            // Set the authentication context if the token is valid
            setAuthenticationContext(token, request);
        } catch (Exception ex) {
            // If an error occurs during authentication, respond with an error
            ResponseBuilder.buildErrorResponse(response, "Authentication error: " + ex.getMessage(),
                    HttpStatus.UNAUTHORIZED);
            return;
        }

        // Continue with the filter chain
        filterChain.doFilter(request, response);
    }

    /**
     * Checks if the request has an Authorization header with a Bearer token.
     * 
     * @param request the HttpServletRequest
     * @return true if the header is present and starts with "Bearer", false
     *         otherwise
     */
    private boolean hasAuthorizationBearer(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        return !ObjectUtils.isEmpty(header) && header.startsWith("Bearer");
    }

    /**
     * Extracts the access token from the Authorization header.
     * 
     * @param request the HttpServletRequest
     * @return the access token
     */
    private String getAccessToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        return header.split(" ")[1].trim();
    }

    /**
     * Sets the authentication context in the security context holder.
     * 
     * @param token   the JWT token
     * @param request the HttpServletRequest
     */
    private void setAuthenticationContext(String token, HttpServletRequest request) {
        UserDetails userDetails = getUserDetails(token);

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null,
                userDetails.getAuthorities());

        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    /**
     * Retrieves user details from the JWT token.
     * 
     * @param token the JWT token
     * @return the user details
     */
    private UserDetails getUserDetails(String token) {
        UserEntity userDetails = new UserEntity();
        String[] jwtSubject = jwtUtil.getSubject(token).split(",");

        userDetails.setId(Integer.parseInt(jwtSubject[0]));
        userDetails.setEmail(jwtSubject[1]);

        return userDetails;
    }
}
