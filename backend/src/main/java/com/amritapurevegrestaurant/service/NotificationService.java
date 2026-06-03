package com.amritapurevegrestaurant.service;

import com.amritapurevegrestaurant.model.Order;
import com.amritapurevegrestaurant.model.Reservation;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;

/**
 * Service responsible for sending asynchronous notifications, primarily email confirmations.
 * This service uses Spring's JavaMailSender and Thymeleaf for templated HTML emails.
 * Methods are marked with @Async to ensure non-blocking execution.
 */
@Service
@Slf4j
public class NotificationService {

    private final JavaMailSender javaMailSender;
    private final SpringTemplateEngine templateEngine;

    @Value("${app.email.sender:noreply@amritapurevegrestaurant.com}")
    private String senderEmail;

    @Value("${app.restaurant.name:Amrita Pure Veg Restaurant}")
    private String restaurantName;

    @Value("${app.restaurant.address:No. 123, Amrita Pure Veg Restaurant, Sr, 3, Baner Rd, Pune, Maharashtra 411045}")
    private String restaurantAddress;

    public NotificationService(JavaMailSender javaMailSender, SpringTemplateEngine templateEngine) {
        this.javaMailSender = javaMailSender;
        this.templateEngine = templateEngine;
    }

    /**
     * Sends an order confirmation email to the customer asynchronously.
     * The email content is generated using a Thymeleaf template.
     *
     * @param order The Order object containing customer and order details.
     */
    @Async
    public void sendOrderConfirmationEmail(Order order) {
        if (order.getCustomerEmail() == null || order.getCustomerEmail().isEmpty()) {
            log.warn("Cannot send order confirmation email for Order ID {} as customer email is missing.", order.getId());
            return;
        }

        log.info("Attempting to send order confirmation email for Order ID: {} to {}", order.getId(), order.getCustomerEmail());
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, StandardCharsets.UTF_8.name());

            Context context = new Context();
            context.setVariable("customerName", order.getCustomerName());
            context.setVariable("orderId", order.getId().toString().substring(0, 8).toUpperCase()); // Shortened ID for display
            context.setVariable("orderDate", order.getOrderDate().format(DateTimeFormatter.ofPattern("dd MMMM yyyy 'at' HH:mm")));
            context.setVariable("totalAmount", order.getTotalAmount());
            context.setVariable("items", order.getItems());
            context.setVariable("restaurantName", restaurantName);
            context.setVariable("restaurantAddress", restaurantAddress);

            String htmlContent = templateEngine.process("email/order-confirmation", context);

            helper.setTo(order.getCustomerEmail());
            helper.setFrom(senderEmail, restaurantName);
            helper.setSubject(restaurantName + " - Your Order #" + order.getId().toString().substring(0, 8).toUpperCase() + " is Confirmed!");
            helper.setText(htmlContent, true); // true indicates HTML content

            javaMailSender.send(message);
            log.info("Order confirmation email sent successfully for Order ID: {} to {}", order.getId(), order.getCustomerEmail());
        } catch (MessagingException e) {
            log.error("Failed to send order confirmation email for Order ID: {} to {}. Messaging error: {}", order.getId(), order.getCustomerEmail(), e.getMessage(), e);
            // Consider re-queueing or alerting for transient messaging issues
        } catch (Exception e) {
            log.error("An unexpected error occurred while sending order confirmation email for Order ID: {} to {}. Error: {}", order.getId(), order.getCustomerEmail(), e.getMessage(), e);
        }
    }

    /**
     * Sends a reservation confirmation email to the customer asynchronously.
     * The email content is generated using a Thymeleaf template.
     *
     * @param reservation The Reservation object containing customer and reservation details.
     */
    @Async
    public void sendReservationConfirmationEmail(Reservation reservation) {
        if (reservation.getCustomerEmail() == null || reservation.getCustomerEmail().isEmpty()) {
            log.warn("Cannot send reservation confirmation email for Reservation ID {} as customer email is missing.", reservation.getId());
            return;
        }

        log.info("Attempting to send reservation confirmation email for Reservation ID: {} to {}", reservation.getId(), reservation.getCustomerEmail());
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, StandardCharsets.UTF_8.name());

            Context context = new Context();
            context.setVariable("customerName", reservation.getCustomerName());
            context.setVariable("reservationId", reservation.getId().toString().substring(0, 8).toUpperCase()); // Shortened ID for display
            context.setVariable("reservationTime", reservation.getReservationTime().format(DateTimeFormatter.ofPattern("dd MMMM yyyy 'at' HH:mm")));
            context.setVariable("partySize", reservation.getPartySize());
            context.setVariable("restaurantName", restaurantName);
            context.setVariable("restaurantAddress", restaurantAddress);

            String htmlContent = templateEngine.process("email/reservation-confirmation", context);

            helper.setTo(reservation.getCustomerEmail());
            helper.setFrom(senderEmail, restaurantName);
            helper.setSubject(restaurantName + " - Your Reservation #" + reservation.getId().toString().substring(0, 8).toUpperCase() + " is Confirmed!");
            helper.setText(htmlContent, true); // true indicates HTML content

            javaMailSender.send(message);
            log.info("Reservation confirmation email sent successfully for Reservation ID: {} to {}", reservation.getId(), reservation.getCustomerEmail());
        } catch (MessagingException e) {
            log.error("Failed to send reservation confirmation email for Reservation ID: {} to {}. Messaging error: {}", reservation.getId(), reservation.getCustomerEmail(), e.getMessage(), e);
        } catch (Exception e) {
            log.error("An unexpected error occurred while sending reservation confirmation email for Reservation ID: {} to {}. Error: {}", reservation.getId(), reservation.getCustomerEmail(), e.getMessage(), e);
        }
    }
}