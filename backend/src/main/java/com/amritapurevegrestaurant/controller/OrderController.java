package com.amritapurevegrestaurant.controller;

import com.amritapurevegrestaurant.dto.CreateOrderRequestDTO;
import com.amritapurevegrestaurant.dto.OrderResponseDTO;
import com.amritapurevegrestaurant.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    /**
     * Handles new order creation requests.
     * This endpoint receives order details, processes them through the OrderService,
     * and returns the initial order response including Razorpay payment details.
     *
     * @param requestDTO The DTO containing order details.
     * @return A ResponseEntity containing the OrderResponseDTO and HTTP status 201 Created.
     */
    @PostMapping
    public ResponseEntity<OrderResponseDTO> createOrder(@Valid @RequestBody CreateOrderRequestDTO requestDTO) {
        OrderResponseDTO responseDTO = orderService.createOrder(requestDTO);
        return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);
    }
}