package com.amritapurevegrestaurant.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MenuItemDTO {
    /**
     * Unique identifier for the menu item.
     */
    private UUID id;

    /**
     * Name of the menu item.
     */
    private String name;

    /**
     * Description of the item.
     */
    private String description;

    /**
     * Price of the item.
     */
    private BigDecimal price;

    /**
     * Category of the item.
     */
    private String category;

    /**
     * URL for the item's image.
     */
    private String imageUrl;

    /**
     * Indicates if the item is a special.
     */
    private boolean isSpecial;
}