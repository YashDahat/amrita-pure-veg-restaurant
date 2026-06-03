import { useMutation, UseMutationResult } from '@tanstack/react-query';
import { reservationService } from '../services/reservationService';
import { CreateReservationRequest, ReservationResponse } from '../types';

/**
 * Hook to create a new reservation.
 * @returns A mutation object with `mutate` function, `isLoading`, `isError`, `isSuccess`, `data`, `error`.
 */
export const useCreateReservation = (): UseMutationResult<ReservationResponse, Error, CreateReservationRequest> => {
  return useMutation<ReservationResponse, Error, CreateReservationRequest>({
    mutationFn: reservationService.createReservation,
    mutationKey: ['createReservation'],
    // Optional: Add onSuccess, onError callbacks here for global side effects like toast notifications
    // or invalidating other queries if a successful reservation affects other data.
    // For example:
    // onSuccess: (data) => {
    //   // Potentially invalidate a query that fetches all reservations for an admin view, if one existed.
    //   // queryClient.invalidateQueries(['reservations']);
    //   console.log('Reservation created successfully:', data);
    // },
    // onError: (error) => {
    //   console.error('Failed to create reservation:', error);
    // },
  });
};