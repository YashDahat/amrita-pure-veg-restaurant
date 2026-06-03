package com.amritapurevegrestaurant.service;

import com.amritapurevegrestaurant.dto.MenuItemDTO;
import com.amritapurevegrestaurant.exception.ResourceNotFoundException;
import com.amritapurevegrestaurant.model.MenuItem;
import com.amritapurevegrestaurant.repository.MenuItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Business logic for managing the menu.
 * This service handles operations related to retrieving menu items,
 * applying filters, and mapping entities to DTOs.
 */
@Service
@RequiredArgsConstructor
public class MenuService {

    private final MenuItemRepository menuItemRepository;

    /**
     * Retrieves all menu items, optionally filtered by category.
     * If a category is provided, it fetches items belonging to that category,
     * sorted by price in ascending order. Otherwise, it retrieves all items.
     *
     * @param category An optional string representing the category to filter by.
     * @return A list of {@link MenuItemDTO} representing the menu items.
     */
    public List<MenuItemDTO> getAllMenuItems(Optional<String> category) {
        List<MenuItem> menuItems;
        if (category.isPresent() && !category.get().isBlank()) {
            menuItems = menuItemRepository.findAllByCategoryNameOrderByPriceAsc(category.get());
        } else {
            menuItems = menuItemRepository.findAll();
        }
        return menuItems.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves a single menu item by its ID.
     *
     * @param id The UUID of the menu item to retrieve.
     * @return A {@link MenuItemDTO} representing the found menu item.
     * @throws ResourceNotFoundException if no menu item with the given ID is found.
     */
    public MenuItemDTO getMenuItemById(UUID id) {
        MenuItem menuItem = menuItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Menu item not found with ID: " + id));
        return convertToDto(menuItem);
    }

    /**
     * Converts a {@link MenuItem} entity to a {@link MenuItemDTO}.
     *
     * @param menuItem The MenuItem entity to convert.
     * @return The corresponding MenuItemDTO.
     */
    private MenuItemDTO convertToDto(MenuItem menuItem) {
        return new MenuItemDTO(
                menuItem.getId(),
                menuItem.getName(),
                menuItem.getDescription(),
                menuItem.getPrice(),
                menuItem.getCategoryName(), // Assuming MenuItem entity has getCategoryName()
                menuItem.getImageUrl(),
                menuItem.isSpecial()
        );
    }
}