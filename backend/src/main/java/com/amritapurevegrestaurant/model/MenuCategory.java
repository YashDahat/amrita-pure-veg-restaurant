package com.amritapurevegrestaurant.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

/**
 * Represents a category of menu items, e.g., 'Appetizers', 'Main Course'.
 */
@Entity
@Table(name = "menu_categories")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MenuCategory {

    /**
     * Primary key, auto-generated.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    /**
     * Name of the category, unique, not null, max 50 chars.
     */
    @Column(name = "name", nullable = false, unique = true, length = 50)
    private String name;

    /**
     * Order in which to display the categories on the menu.
     */
    @Column(name = "display_order", nullable = false)
    private int displayOrder;

    /**
     * List of items in this category, One-to-Many relationship.
     * Mapped by the 'category' field in the MenuItem entity.
     * CascadeType.ALL ensures that all persistence operations (like persist, merge, remove)
     * on MenuCategory are cascaded to its associated MenuItem entities.
     * orphanRemoval = true means that if a MenuItem is removed from the 'menuItems' collection,
     * it will be automatically deleted from the database.
     * FetchType.LAZY is used to load menu items only when they are explicitly accessed.
     */
    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<MenuItem> menuItems;
}