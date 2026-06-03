package com.amritapurevegrestaurant.service;

import com.amritapurevegrestaurant.dto.CreateOrderRequestDTO;
import com.amritapurevegrestaurant.dto.OrderItemRequestDTO;
import com.amritapurevegrestaurant.dto.OrderResponseDTO;
import com.amritapurevegrestaurant.enums.OrderStatus;
import com.amritapurevegrestaurant.exception.ResourceNotFoundException;
import com.amritapurevegrestaurant.model.MenuItem;
import com.amritapurevegrestaurant.model.Order;
import com.amritapurevegrestaurant.model.OrderItem;
import com.amritapurevegrestaurant.repository.MenuItemRepository;
import com.amritapurevegrestaurant.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final MenuItemRepository menuItemRepository;
    private final PaymentService paymentService;
    private final NotificationService notificationService;

    @Value("${razorpay.key_id}")
    private String razorpayApiKey;

    /**
     * Creates a new order, calculates total, and initiates payment with Razorpay.
     *
     * @param requestDTO The DTO containing order details.
     * @return An OrderResponseDTO with initial order status and Razorpay payment details.
     * @throws ResourceNotFoundException if any menu item in the order is not found.
     * @throws IllegalArgumentException if an order item has an invalid quantity (<= 0).
     * @throws IllegalStateException if there's an issue with payment initiation.
     */
    @Transactional
    public OrderResponseDTO createOrder(CreateOrderRequestDTO requestDTO) {
        Order order = new Order();
        order.setCustomerName(requestDTO.getCustomerName());
        order.setCustomerPhone(requestDTO.getCustomerPhone());
        order.setCustomerEmail(requestDTO.getCustomerEmail());
        order.setStatus(OrderStatus.PENDING_PAYMENT); // Initial status

        BigDecimal totalAmount = BigDecimal.ZERO;
        for (OrderItemRequestDTO itemDTO : requestDTO.getItems()) {
            MenuItem menuItem = menuItemRepository.findById(itemDTO.getMenuItemId())
                    .orElseThrow(() -> new ResourceNotFoundException("Menu item not found with ID: " + itemDTO.getMenuItemId()));

            if (itemDTO.getQuantity() <= 0) {
                throw new IllegalArgumentException("Quantity for menu item '" + menuItem.getName() + "' must be at least 1.");
            }

            OrderItem orderItem = new OrderItem();
            orderItem.setMenuItem(menuItem);
            orderItem.setQuantity(itemDTO.getQuantity());
            orderItem.setPriceAtOrder(menuItem.getPrice()); // Capture price at the time of order
            order.addOrderItem(orderItem);

            totalAmount = totalAmount.add(menuItem.getPrice().multiply(BigDecimal.valueOf(itemDTO.getQuantity())));
        }

        order.setTotalAmount(totalAmount);
        Order savedOrder = orderRepository.save(order); // Save to generate ID for receipt

        // Initiate payment with Razorpay
        try {
            String razorpayOrderId = paymentService.createRazorpayOrder(savedOrder.getTotalAmount(), savedOrder.getId().toString());
            savedOrder.setRazorpayOrderId(razorpayOrderId);
            orderRepository.save(savedOrder); // Update order with Razorpay ID
        } catch (Exception e) {
            // Log the exception and potentially throw a more specific business exception.
            // The transaction will roll back due to the runtime exception.
            throw new IllegalStateException("Failed to initiate Razorpay payment for order: " + savedOrder.getId(), e);
        }

        return new OrderResponseDTO(
                savedOrder.getId(),
                savedOrder.getStatus().name(),
                savedOrder.getTotalAmount(),
                savedOrder.getRazorpayOrderId(),
                razorpayApiKey
        );
    }

    /**
     * Verifies Razorpay payment signature and updates order status to CONFIRMED.
     * This method is typically called by a webhook handler after a successful payment.
     *
     * @param razorpayOrderId The order ID from Razorpay.
     * @param razorpayPaymentId The payment ID from Razorpay.
     * @param razorpaySignature The signature received from Razorpay webhook.
     * @throws ResourceNotFoundException if the order with the given Razorpay Order ID is not found.
     * @throws SecurityException if the Razorpay payment signature verification fails.
     */
    @Transactional
    public void confirmOrderPayment(String razorpayOrderId, String razorpayPaymentId, String razorpaySignature) {
        Order order = orderRepository.findByRazorpayOrderId(razorpayOrderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with Razorpay Order ID: " + razorpayOrderId));

        if (order.getStatus() == OrderStatus.CONFIRMED) {
            // Idempotency: order is already confirmed, no need to reprocess
            return;
        }

        // Verify the payment signature with Razorpay
        boolean isSignatureValid = paymentService.verifyPaymentSignature(razorpayOrderId, razorpayPaymentId, razorpaySignature);

        if (!isSignatureValid) {
            // Log this as a potential fraud attempt or misconfiguration
            throw new SecurityException("Razorpay payment signature verification failed for order: " + razorpayOrderId);
        }

        order.setRazorpayPaymentId(razorpayPaymentId);
        order.setStatus(OrderStatus.CONFIRMED);
        orderRepository.save(order);

        // Asynchronously send order confirmation email
        notificationService.sendOrderConfirmationEmail(order);
    }

    /**
     * Retrieves full order details by its ID.
     *
     * @param orderId The UUID of the order.
     * @return The Order entity.
     * @throws ResourceNotFoundException if the order is not found.
     */
    @Transactional(readOnly = true)
    public Order getOrderById(UUID orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with ID: " + orderId));
    }
}