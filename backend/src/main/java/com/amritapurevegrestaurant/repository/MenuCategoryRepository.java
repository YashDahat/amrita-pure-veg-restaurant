package com.amritapurevegrestaurant.repository;

import com.amritapurevegrestaurant.model.MenuCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MenuCategoryRepository extends JpaRepository<MenuCategory, UUID> {

    /**
     * Finds all menu categories, ordered by their display order in ascending manner.
     *
     * @return a list of all MenuCategory entities, sorted by displayOrder.
     */
    List<MenuCategory> findAllByOrderByDisplayOrderAsc();
}