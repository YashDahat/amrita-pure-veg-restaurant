export type OrderStatus =
  | "PENDING_PAYMENT"
  | "CONFIRMED"
  | "PREPARING"
  | "READY_FOR_PICKUP"
  | "COMPLETED"
  | "CANCELLED";

export type ReservationStatus =
  | "REQUESTED"
  | "CONFIRMED"
  | "CANCELLED"
  | "COMPLETED";

export interface MenuItem {
  id: string;
  name: string;
  description: string;
  price: number;
  category: string;
  imageUrl: string;
  isSpecial: boolean;
}

export interface OrderItemRequest {
  menuItemId: string;
  quantity: number;
}

export interface CreateOrderRequest {
  customerName: string;
  customerPhone: string;
  customerEmail: string;
  items: OrderItemRequest[];
}

export interface OrderResponse {
  orderId: string;
  status: OrderStatus;
  totalAmount: number;
  razorpayOrderId: string;
  razorpayApiKey: string;
}

export interface CreateReservationRequest {
  customerName: string;
  customerPhone: string;
  customerEmail: string;
  reservationTime: string; // ISO 8601 string (e.g., "2024-05-22T10:30:00")
  partySize: number;
}

export interface ReservationResponse {
  reservationId: string;
  customerName: string;
  reservationTime: string; // ISO 8601 string (e.g., "2024-05-22T10:30:00")
  partySize: number;
  status: ReservationStatus;
}

export interface ErrorResponse {
  timestamp: string; // ISO 8601 string
  status: number;
  error: string;
  message: string;
  path: string;
}