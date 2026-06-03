package com.amritapurevegrestaurant.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {
    /**
     * Timestamp of when the error occurred.
     */
    private LocalDateTime timestamp;

    /**
     * HTTP status code.
     */
    private int status;

    /**
     * Short error description (e.g., 'Not Found').
     */
    private String error;

    /**
     * Detailed error message.
     */
    private String message;

    /**
     * The request path that caused the error.
     */
    private String path;
}