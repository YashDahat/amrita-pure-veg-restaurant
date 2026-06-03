package com.amritapurevegrestaurant.exception;

import com.amritapurevegrestaurant.dto.ErrorResponse;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Centralized exception handler for the REST API.
 * This class extends {@link ResponseEntityExceptionHandler} to leverage Spring's default exception handling
 * and provides custom handling for specific exceptions to return consistent {@link ErrorResponse} objects.
 */
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    /**
     * Handles {@link ResourceNotFoundException}, returning a 404 Not Found status.
     * This exception is typically thrown when a requested resource (e.g., MenuItem, Order) does not exist.
     *
     * @param ex      The {@link ResourceNotFoundException} instance.
     * @param request The current web request.
     * @return A {@link ResponseEntity} containing an {@link ErrorResponse} and HTTP status 404.
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(ResourceNotFoundException ex, WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.NOT_FOUND.value(),
                HttpStatus.NOT_FOUND.getReasonPhrase(),
                ex.getMessage(),
                request.getDescription(false)
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    /**
     * Handles {@link MethodArgumentNotValidException}, which occurs when validation on an argument annotated with @Valid fails.
     * This method overrides the default handling in {@link ResponseEntityExceptionHandler} to return a custom {@link ErrorResponse}
     * with detailed validation errors.
     *
     * @param ex      The {@link MethodArgumentNotValidException} instance.
     * @param headers The headers to be written to the response.
     * @param status  The status code to use for the response.
     * @param request The current web request.
     * @return A {@link ResponseEntity} containing an {@link ErrorResponse} and HTTP status 400.
     */
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {

        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.toList());

        String errorMessage = "Validation failed: " + String.join(", ", errors);

        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                errorMessage,
                request.getDescription(false)
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles {@link IllegalArgumentException}, returning a 400 Bad Request status.
     * This exception is typically thrown when a method has been passed an illegal or inappropriate argument.
     *
     * @param ex      The {@link IllegalArgumentException} instance.
     * @param request The current web request.
     * @return A {@link ResponseEntity} containing an {@link ErrorResponse} and HTTP status 400.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex, WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                ex.getMessage(),
                request.getDescription(false)
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles {@link ConstraintViolationException}, which occurs when validation fails for method parameters
     * (e.g., {@code @RequestParam}, {@code @PathVariable}) or return values.
     *
     * @param ex      The {@link ConstraintViolationException} instance.
     * @param request The current web request.
     * @return A {@link ResponseEntity} containing an {@link ErrorResponse} and HTTP status 400.
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolationException(ConstraintViolationException ex, WebRequest request) {
        List<String> errors = ex.getConstraintViolations()
                .stream()
                .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
                .collect(Collectors.toList());

        String errorMessage = "Validation failed: " + String.join(", ", errors);

        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                errorMessage,
                request.getDescription(false)
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Catches all other unhandled {@link Exception} types, returning a 500 Internal Server Error.
     * This acts as a fallback for any unexpected errors, preventing sensitive information from being exposed
     * and providing a consistent error response.
     *
     * @param ex      The {@link Exception} instance.
     * @param request The current web request.
     * @return A {@link ResponseEntity} containing an {@link ErrorResponse} and HTTP status 500.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAllExceptions(Exception ex, WebRequest request) {
        // Log the exception for debugging purposes. Using the logger from ResponseEntityExceptionHandler.
        logger.error("An unexpected error occurred: {}", ex.getMessage(), ex);

        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                "An unexpected error occurred. Please try again later.",
                request.getDescription(false)
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}