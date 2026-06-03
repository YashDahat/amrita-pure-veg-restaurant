package com.amritapurevegrestaurant.controller;

import com.amritapurevegrestaurant.dto.MenuItemDTO;
import com.amritapurevegrestaurant.service.MenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * REST controller for menu-related endpoints.
 * Provides API endpoints for retrieving menu items.
 */
@RestController
@RequestMapping("/api/v1/menu")
@RequiredArgsConstructor
public class MenuController {

    private final MenuService menuService;

    /**
     * Handles requests to fetch menu items.
     * This endpoint allows fetching all menu items or filtering them by category.
     *
     * @param category An optional query parameter to filter menu items by category.
     * @return A {@link ResponseEntity} containing a list of {@link MenuItemDTO}
     *         and an HTTP status of 200 OK.
     */
    @GetMapping("/items")
    public ResponseEntity<List<MenuItemDTO>> getMenu(@RequestParam Optional<String> category) {
        List<MenuItemDTO> menuItems = menuService.getAllMenuItems(category);
        return ResponseEntity.ok(menuItems);
    }

    /**
     * Handles requests to fetch a single menu item by its unique identifier.
     *
     * @param id The UUID of the menu item to retrieve, provided as a path variable.
     * @return A {@link ResponseEntity} containing the {@link MenuItemDTO} of the
     *         requested item and an HTTP status of 200 OK.
     * @throws com.amritapurevegrestaurant.exception.ResourceNotFoundException if no menu item
     *         with the given ID is found. This is handled by {@link com.amritapurevegrestaurant.exception.GlobalExceptionHandler}.
     */
    @GetMapping("/items/{id}")
    public ResponseEntity<MenuItemDTO> getMenuItem(@PathVariable UUID id) {
        MenuItemDTO menuItem = menuService.getMenuItemById(id);
        return ResponseEntity.ok(menuItem);
    }
}