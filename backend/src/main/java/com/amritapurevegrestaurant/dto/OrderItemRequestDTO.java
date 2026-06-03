package com.amritapurevegrestaurant.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemRequestDTO {

    /**
     * ID of the menu item.
     */
    @NotNull(message = "Menu item ID cannot be null")
    private UUID menuItemId;

    /**
     * Quantity of the item.
     */
    @Min(value = 1, message = "Quantity must be at least 1")
    private int quantity;
}