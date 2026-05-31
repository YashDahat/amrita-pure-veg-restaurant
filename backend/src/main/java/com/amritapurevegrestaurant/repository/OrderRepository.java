package com.amritapurevegrestaurant.repository;

import com.amritapurevegrestaurant.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {

    /**
     * Finds an order by its associated Razorpay Order ID.
     * @param razorpayOrderId The Razorpay Order ID.
     * @return An Optional containing the Order if found, or empty otherwise.
     */
    Optional<Order> findByRazorpayOrderId(String razorpayOrderId);
}