package com.amritapurevegrestaurant.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrderRequestDTO {

    @NotBlank(message = "Customer name cannot be blank")
    private String customerName;

    @NotBlank(message = "Customer phone number cannot be blank")
    @Pattern(regexp = "^[+]?[0-9]{10,15}$", message = "Invalid phone number format. Must be 10-15 digits, optionally starting with '+'.")
    private String customerPhone;

    @NotBlank(message = "Customer email cannot be blank")
    @Email(message = "Invalid email address format")
    private String customerEmail;

    @NotEmpty(message = "Order must contain at least one item")
    @Valid // Ensures validation of nested OrderItemRequestDTOs
    private List<OrderItemRequestDTO> items;
}