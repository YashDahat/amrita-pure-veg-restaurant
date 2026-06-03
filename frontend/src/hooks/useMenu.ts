import { useQuery, UseQueryResult } from "@tanstack/react-query";
import menuService from "../services/menuService";
import { MenuItem } from "../types";

/**
 * React Query hook to fetch all menu items.
 *
 * This hook provides a convenient way to fetch and cache menu items using React Query.
 * It automatically manages loading, error, and data states, making it easy for components
 * to consume menu data without dealing with direct API calls or complex state management.
 *
 * @param category An optional category string to filter the menu items.
 *                 If provided, only items belonging to that category will be fetched.
 * @returns A `UseQueryResult` object from React Query, containing:
 *          - `data`: An array of `MenuItem` objects if the query was successful.
 *          - `isLoading`: A boolean indicating if the data is currently being fetched.
 *          - `isError`: A boolean indicating if an error occurred during fetching.
 *          - `error`: The error object if `isError` is true.
 *          - Other React Query properties for advanced usage.
 */
export const useMenuItems = (category?: string): UseQueryResult<MenuItem[], Error> => {
  return useQuery<MenuItem[], Error>({
    queryKey: ["menuItems", category], // Unique key for caching, includes category for filtered lists
    queryFn: () => menuService.getMenuItems(category), // Function to fetch the data
  });
};

/**
 * React Query hook to fetch a single menu item by its ID.
 *
 * @param id The unique identifier (UUID string) of the menu item to fetch.
 * @returns A `UseQueryResult` object for a single `MenuItem`.
 */
export const useMenuItem = (id: string): UseQueryResult<MenuItem, Error> => {
  return useQuery<MenuItem, Error>({
    queryKey: ["menuItem", id], // Unique key for caching a single item
    queryFn: () => menuService.getMenuItemById(id), // Function to fetch the data
    enabled: !!id, // Only run the query if an ID is provided
  });
};