import apiClient from "../api/client";
import { MenuItem } from "../types";

/**
 * Provides functions to interact with the menu API endpoints.
 * This service layer is responsible for making HTTP requests to the backend
 * and mapping the responses to frontend types.
 */
const menuService = {
  /**
   * Fetches all menu items from the backend API.
   * Optionally filters menu items by category.
   *
   * @param category An optional category string to filter menu items.
   * @returns A promise that resolves to an array of MenuItem objects.
   * @throws An error if the API call fails.
   */
  getMenuItems: async (category?: string): Promise<MenuItem[]> => {
    try {
      const response = await apiClient.get<MenuItem[]>("/api/v1/menu/items", {
        params: category ? { category } : {},
      });
      return response.data;
    } catch (error) {
      console.error("Failed to fetch menu items:", error);
      throw error; // Re-throw to be handled by the calling hook/component
    }
  },

  /**
   * Fetches a single menu item by its unique identifier.
   *
   * @param id The UUID (as a string) of the menu item to fetch.
   * @returns A promise that resolves to a single MenuItem object.
   * @throws An error if the API call fails or the item is not found.
   */
  getMenuItemById: async (id: string): Promise<MenuItem> => {
    try {
      const response = await apiClient.get<MenuItem>(`/api/v1/menu/items/${id}`);
      return response.data;
    } catch (error) {
      console.error(`Failed to fetch menu item with ID ${id}:`, error);
      throw error; // Re-throw to be handled by the calling hook/component
    }
  },
};

export default menuService;