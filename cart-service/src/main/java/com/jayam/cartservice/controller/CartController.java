package com.jayam.cartservice.controller;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jayam.cartservice.client.OrderServiceClient;
import com.jayam.cartservice.dto.OrderRequest;
import com.jayam.cartservice.dto.OrderResponse;
import com.jayam.cartservice.exception.BusinessException;

/**
 * Cart Controller - Handles cart operations and checkout
 * 
 * Exception handling is managed by GlobalExceptionHandler
 * This controller throws exceptions which are automatically caught and formatted
 */
@RestController
@RequestMapping("/cart")
public class CartController {
    
    private static final Logger logger = LoggerFactory.getLogger(CartController.class);
    
    private final OrderServiceClient orderServiceClient;

    public CartController(OrderServiceClient orderServiceClient) {
        this.orderServiceClient = orderServiceClient;
    }

    /**
     * Checkout endpoint - Creates an order from cart items
     * 
     * @param cartRequest Map containing cart items
     * @return ResponseEntity with checkout result
     * @throws BusinessException if cart is empty or invalid
     * @throws FeignException if order service communication fails (caught by GlobalExceptionHandler)
     */
    @PostMapping("/checkout")
    public ResponseEntity<Map<String, Object>> checkout(@RequestBody Map<String, Object> cartRequest) {
        logger.info("Checkout initiated for cart request");
        
        // Validate cart request
        if (cartRequest == null || !cartRequest.containsKey("items")) {
            throw new BusinessException("INVALID_CART", "Cart request must contain 'items' field");
        }
        
        // Extract items from cart request
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> items = (List<Map<String, Object>>) cartRequest.get("items");
        
        // Validate items
        if (items == null || items.isEmpty()) {
            throw new BusinessException("EMPTY_CART", "Cannot checkout with empty cart");
        }
        
        logger.debug("Processing checkout for {} items", items.size());
        
        // Create order request using Feign client
        // If order service is unavailable, FeignException will be thrown
        // GlobalExceptionHandler will catch and handle it automatically
        OrderRequest orderRequest = new OrderRequest(items);
        OrderResponse orderResponse = orderServiceClient.createOrder(orderRequest);
        
        // Build success response
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now().toString());
        response.put("status", HttpStatus.OK.value());
        response.put("message", "Checkout initiated successfully");
        
        Map<String, Object> data = new HashMap<>();
        data.put("service", "cart-service");
        data.put("orderResponse", orderResponse);
        response.put("data", data);
        
        logger.info("Checkout completed successfully");
        return ResponseEntity.ok(response);
    }
}
