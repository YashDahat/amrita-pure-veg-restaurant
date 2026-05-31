package com.amritapurevegrestaurant.repository;

import com.amritapurevegrestaurant.model.MenuItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MenuItemRepository extends JpaRepository<MenuItem, UUID> {

    /**
     * Finds all menu items in a given category, sorted by price in ascending order.
     *
     * @param categoryName The name of the category.
     * @return A list of MenuItem entities belonging to the specified category, ordered by price.
     */
    List<MenuItem> findAllByCategoryNameOrderByPriceAsc(String categoryName);
}