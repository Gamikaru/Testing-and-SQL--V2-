package com.rocketFoodDelivery.rocketFood.exception;

/**
 * Exception thrown when validation fails.
 */
public class ValidationException extends RuntimeException {
    /**
     * Constructs a new ValidationException with the specified detail message.
     *
     * @param message the detail message.
     */
    public ValidationException(String message) {
        super(message);
    }
}
