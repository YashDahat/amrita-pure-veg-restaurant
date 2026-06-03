import apiClient from '../api/client';
import { CreateReservationRequest, ReservationResponse } from '../types';

const BASE_URL = '/api/v1/reservations';

/**
 * Provides functions to interact with the reservation API endpoints.
 */
export const reservationService = {
  /**
   * Submits a new reservation request to the backend.
   * @param reservation The reservation details.
   * @returns A promise that resolves to the created reservation response.
   * @throws An error if the API call fails.
   */
  createReservation: async (reservation: CreateReservationRequest): Promise<ReservationResponse> => {
    try {
      const response = await apiClient.post<ReservationResponse>(BASE_URL, reservation);
      return response.data;
    } catch (error) {
      console.error('Error creating reservation:', error);
      throw error;
    }
  },

  /**
   * Fetches available reservation slots for a given date.
   * @param date The date in 'YYYY-MM-DD' format (e.g., "2024-12-25").
   * @returns A promise that resolves to an array of available time slots as strings (e.g., ["18:00:00", "19:30:00"]).
   * @throws An error if the API call fails.
   */
  getAvailableSlots: async (date: string): Promise<string[]> => {
    try {
      const response = await apiClient.get<string[]>(`${BASE_URL}/slots`, {
        params: { date },
      });
      return response.data;
    } catch (error) {
      console.error(`Error fetching available slots for date ${date}:`, error);
      throw error;
    }
  },
};