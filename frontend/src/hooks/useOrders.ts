import { useMutation, UseMutationResult } from '@tanstack/react-query';
import { orderService } from '../services/orderService';
import { CreateOrderRequest, OrderResponse } from '../types';

/**
 * React Query hook for creating a new order.
 *
 * This hook provides a mutation function to submit new order details to the backend.
 * It leverages `useMutation` from `@tanstack/react-query` to manage the asynchronous
 * state (loading, error, success) of the order creation process.
 *
 * @returns A mutation object with properties like `mutate` (the function to call for creating an order),
 *          `isLoading`, `isError`, `isSuccess`, `data` (the OrderResponse on success), and `error`.
 */
export const useCreateOrder = (): UseMutationResult<OrderResponse, Error, CreateOrderRequest> => {
  return useMutation<OrderResponse, Error, CreateOrderRequest>({
    mutationFn: (order: CreateOrderRequest) => orderService.createOrder(order),
    // Optional: Add callbacks for side effects after mutation
    // For example, to invalidate queries, show notifications, or redirect:
    // onSuccess: (data) => {
    //   console.log('Order created successfully:', data);
    //   // queryClient.invalidateQueries(['orders']); // If there was a query to list orders
    //   // toast.success('Order placed successfully!');
    // },
    // onError: (error) => {
    //   console.error('Failed to create order:', error);
    //   // toast.error('Failed to place order. Please try again.');
    // },
  });
};