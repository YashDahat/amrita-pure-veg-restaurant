import React, { createContext, useContext, useState, useEffect, useMemo, useCallback } from 'react';
import { MenuItem } from '../types';

/**
 * Represents a single item in the shopping cart, extending MenuItem with a quantity.
 */
interface CartItem extends MenuItem {
  quantity: number;
}

/**
 * Defines the shape of the CartContext, including cart state and actions.
 */
interface CartContextType {
  cartItems: CartItem[];
  totalItems: number;
  totalPrice: number;
  addToCart: (item: MenuItem, quantity?: number) => void;
  removeFromCart: (itemId: string) => void;
  updateQuantity: (itemId: string, quantity: number) => void;
  clearCart: () => void;
}

// Create the context with an undefined default value, which will be provided by the CartProvider.
const CartContext = createContext<CartContextType | undefined>(undefined);

/**
 * CartProvider component that manages the state of the shopping cart.
 * It persists the cart state to localStorage and provides it to its children.
 */
export const CartProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [cartItems, setCartItems] = useState<CartItem[]>(() => {
    // Initialize cart state from localStorage on component mount.
    // This ensures the cart persists across page reloads.
    if (typeof window !== 'undefined') {
      try {
        const savedCart = localStorage.getItem('amritaPureVegCart');
        return savedCart ? JSON.parse(savedCart) : [];
      } catch (error) {
        console.error('Failed to parse cart from localStorage:', error);
        return [];
      }
    }
    return [];
  });

  // Effect to persist cartItems to localStorage whenever they change.
  useEffect(() => {
    if (typeof window !== 'undefined') {
      localStorage.setItem('amritaPureVegCart', JSON.stringify(cartItems));
    }
  }, [cartItems]);

  // Calculate the total number of items in the cart (sum of quantities).
  const totalItems = useMemo(() => {
    return cartItems.reduce((sum, item) => sum + item.quantity, 0);
  }, [cartItems]);

  // Calculate the total price of all items in the cart.
  const totalPrice = useMemo(() => {
    return cartItems.reduce((sum, item) => sum + item.price * item.quantity, 0);
  }, [cartItems]);

  /**
   * Adds a MenuItem to the cart or increments its quantity if it already exists.
   * @param item The MenuItem to add.
   * @param quantityToAdd The quantity to add, defaults to 1.
   */
  const addToCart = useCallback((item: MenuItem, quantityToAdd: number = 1) => {
    setCartItems((prevItems) => {
      const existingItemIndex = prevItems.findIndex((cartItem) => cartItem.id === item.id);

      if (existingItemIndex > -1) {
        // Item already exists, update its quantity
        const updatedItems = [...prevItems];
        updatedItems[existingItemIndex] = {
          ...updatedItems[existingItemIndex],
          quantity: updatedItems[existingItemIndex].quantity + quantityToAdd,
        };
        return updatedItems;
      } else {
        // Item does not exist, add it as a new cart item
        return [...prevItems, { ...item, quantity: quantityToAdd }];
      }
    });
  }, []);

  /**
   * Removes an item completely from the cart.
   * @param itemId The ID of the item to remove.
   */
  const removeFromCart = useCallback((itemId: string) => {
    setCartItems((prevItems) => prevItems.filter((item) => item.id !== itemId));
  }, []);

  /**
   * Updates the quantity of a specific item in the cart.
   * If the new quantity is 0 or less, the item is removed from the cart.
   * @param itemId The ID of the item to update.
   * @param newQuantity The new quantity for the item.
   */
  const updateQuantity = useCallback((itemId: string, newQuantity: number) => {
    setCartItems((prevItems) => {
      if (newQuantity <= 0) {
        return prevItems.filter((item) => item.id !== itemId);
      }

      const existingItemIndex = prevItems.findIndex((cartItem) => cartItem.id === itemId);
      if (existingItemIndex > -1) {
        const updatedItems = [...prevItems];
        updatedItems[existingItemIndex] = {
          ...updatedItems[existingItemIndex],
          quantity: newQuantity,
        };
        return updatedItems;
      }
      return prevItems; // Item not found, return original state
    });
  }, []);

  /**
   * Clears all items from the shopping cart.
   */
  const clearCart = useCallback(() => {
    setCartItems([]);
  }, []);

  // Memoize the context value to prevent unnecessary re-renders of consumers.
  const contextValue = useMemo(
    () => ({
      cartItems,
      totalItems,
      totalPrice,
      addToCart,
      removeFromCart,
      updateQuantity,
      clearCart,
    }),
    [cartItems, totalItems, totalPrice, addToCart, removeFromCart, updateQuantity, clearCart]
  );

  return <CartContext.Provider value={contextValue}>{children}</CartContext.Provider>;
};

/**
 * Custom hook to consume the CartContext.
 * Throws an error if used outside of a CartProvider.
 * @returns The CartContextType object.
 */
export const useCart = () => {
  const context = useContext(CartContext);
  if (context === undefined) {
    throw new Error('useCart must be used within a CartProvider');
  }
  return context;
};