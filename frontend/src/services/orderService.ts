import apiClient from '../api/client';
import { CreateOrderRequest, OrderResponse } from '../types';

/**
 * Provides functions to interact with the order API endpoints.
 */
export const orderService = {
  /**
   * Submits a new order to the backend.
   * @param order The order details to create.
   * @returns A promise that resolves with the created order's response, including Razorpay details.
   */
  createOrder: async (order: CreateOrderRequest): Promise<OrderResponse> => {
    try {
      const response = await apiClient.post<OrderResponse>('/api/v1/orders', order);
      return response.data;
    } catch (error) {
      console.error('Failed to create order:', error);
      throw error; // Re-throw to allow higher-level error handling (e.g., in React Query hooks)
    }
  },
};