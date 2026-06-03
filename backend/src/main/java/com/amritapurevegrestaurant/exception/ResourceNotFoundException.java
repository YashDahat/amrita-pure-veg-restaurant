package com.amritapurevegrestaurant.exception;

/**
 * Custom exception for cases where a requested resource is not found.
 * This exception is typically thrown by service layers when an entity cannot be located
 * by its identifier, and it is handled by {@link GlobalExceptionHandler} to return
 * an appropriate HTTP 404 Not Found response.
 */
public class ResourceNotFoundException extends RuntimeException {

    /**
     * Constructs a new ResourceNotFoundException with the specified detail message.
     *
     * @param message The detail message (which is saved for later retrieval by the {@link #getMessage()} method).
     */
    public ResourceNotFoundException(String message) {
        super(message);
    }

    /**
     * Constructs a new ResourceNotFoundException with the specified detail message and cause.
     *
     * @param message The detail message.
     * @param cause   The cause (which is saved for later retrieval by the {@link #getCause()} method).
     *                (A null value is permitted, and indicates that the cause is nonexistent or unknown.)
     */
    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}