package com.rocketFoodDelivery.rocketFood.security;

import com.rocketFoodDelivery.rocketFood.models.UserEntity;
import io.jsonwebtoken.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * Utility class for handling JWT operations.
 * This includes generating, validating, and parsing JWT tokens.
 */
@Component
public class JwtUtil {

    @Value("${app.jwt.secret}")
    private String secret;

    private static final Logger LOGGER = LoggerFactory.getLogger(JwtUtil.class);

    /**
     * Validates that the secret key is not null or empty.
     */
    @PostConstruct
    public void validateSecret() {
        if (secret == null || secret.isEmpty()) {
            throw new IllegalArgumentException("JWT secret cannot be null or empty");
        }
    }

    /**
     * Generates an access token for the user.
     * 
     * @param user the UserEntity
     * @return the generated JWT token
     */
    public String generateAccessToken(UserEntity user) {
        return Jwts.builder()
                .setSubject(String.format("%s,%s", user.getId(), user.getEmail()))
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
    }

    /**
     * Validates the access token.
     * 
     * @param token the JWT token
     * @return true if the token is valid, false otherwise
     */
    public boolean validateAccessToken(String token) {
        try {
            Jwts.parser().setSigningKey(secret).parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException ex) {
            LOGGER.error("JWT expired: {}", ex.getMessage());
        } catch (IllegalArgumentException ex) {
            LOGGER.error("Token is null, empty or only whitespace: {}", ex.getMessage());
        } catch (MalformedJwtException ex) {
            LOGGER.error("JWT is invalid: {}", ex.getMessage());
        } catch (UnsupportedJwtException ex) {
            LOGGER.error("JWT is not supported: {}", ex.getMessage());
        } catch (SignatureException ex) {
            LOGGER.error("Signature validation failed: {}", ex.getMessage());
        }
        return false;
    }

    /**
     * Extracts the subject (user information) from the token.
     * 
     * @param token the JWT token
     * @return the subject
     */
    public String getSubject(String token) {
        return parseClaims(token).getSubject();
    }

    /**
     * Parses the claims from the token.
     * 
     * @param token the JWT token
     * @return the claims
     */
    private Claims parseClaims(String token) {
        return Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(token)
                .getBody();
    }
}
