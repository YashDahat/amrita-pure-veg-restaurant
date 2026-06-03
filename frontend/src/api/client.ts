import axios, { AxiosInstance } from 'axios';

// Determine the API base URL from environment variables.
// In a Vite project, environment variables are accessed via import.meta.env.
// Fallback to localhost for development if the variable is not set.
const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080';

/**
 * Configured Axios instance for making HTTP requests to the backend API.
 * This singleton client ensures consistent base URL, headers, and potential
 * interceptors across all API calls in the frontend.
 */
const apiClient: AxiosInstance = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
    // Add any other default headers here, e.g., Authorization for JWT tokens
    // 'Authorization': `Bearer ${localStorage.getItem('authToken')}`
  },
  // Set a default timeout for all requests (e.g., 10 seconds)
  timeout: 10000,
});

// Optional: Add request interceptors for common tasks like adding auth tokens
// apiClient.interceptors.request.use(
//   (config) => {
//     const token = localStorage.getItem('authToken'); // Assuming token is stored in localStorage
//     if (token) {
//       config.headers.Authorization = `Bearer ${token}`;
//     }
//     return config;
//   },
//   (error) => {
//     return Promise.reject(error);
//   }
// );

// Optional: Add response interceptors for global error handling (e.g., redirect on 401)
// apiClient.interceptors.response.use(
//   (response) => response,
//   (error) => {
//     if (error.response && error.response.status === 401) {
//       // Handle unauthorized errors, e.g., redirect to login
//       console.error('Unauthorized access, redirecting to login...');
//       // window.location.href = '/login';
//     }
//     return Promise.reject(error);
//   }
// );

export default apiClient;