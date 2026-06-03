package com.amritapurevegrestaurant.model;

import com.amritapurevegrestaurant.enums.OrderStatus;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "orders") // Explicitly name table to avoid conflict with SQL keyword 'ORDER'
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 100)
    private String customerName;

    @Column(nullable = false, length = 20) // Assuming a reasonable max length for phone numbers
    private String customerPhone;

    @Column(nullable = false, length = 100)
    private String customerEmail;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items = new ArrayList<>();

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    @Column(length = 50) // Assuming Razorpay Order ID max length
    private String razorpayOrderId;

    @Column(length = 50) // Assuming Razorpay Payment ID max length
    private String razorpayPaymentId;

    @CreationTimestamp
    @Column(name = "order_date", nullable = false, updatable = false)
    private LocalDateTime orderDate;

    /**
     * Helper method to add an OrderItem to the order, ensuring bidirectional relationship consistency.
     * @param item The OrderItem to add.
     */
    public void addOrderItem(OrderItem item) {
        items.add(item);
        item.setOrder(this);
    }

    /**
     * Helper method to remove an OrderItem from the order, ensuring bidirectional relationship consistency.
     * @param item The OrderItem to remove.
     */
    public void removeOrderItem(OrderItem item) {
        items.remove(item);
        item.setOrder(null);
    }
}