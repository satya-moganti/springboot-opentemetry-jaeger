package com.jayam.orderservice.controller;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jayam.orderservice.client.InventoryServiceClient;
import com.jayam.orderservice.dto.InventoryRequest;
import com.jayam.orderservice.dto.InventoryResponse;
import com.jayam.orderservice.exception.BusinessException;

/**
 * Order Controller - Handles order creation and management
 * 
 * Exception handling is managed by GlobalExceptionHandler
 * This controller throws exceptions which are automatically caught and formatted
 */
@RestController
@RequestMapping("/orders")
public class OrderController {
    
    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);
    
    private final InventoryServiceClient inventoryServiceClient;

    public OrderController(InventoryServiceClient inventoryServiceClient) {
        this.inventoryServiceClient = inventoryServiceClient;
    }

    /**
     * Create order endpoint - Creates an order and reserves inventory
     * 
     * @param orderRequest Map containing order details and items
     * @return ResponseEntity with order creation result
     * @throws BusinessException if order request is empty or invalid
     * @throws FeignException if inventory service communication fails (caught by GlobalExceptionHandler)
     */
    @PostMapping("/create")
    public ResponseEntity<Map<String, Object>> createOrder(@RequestBody Map<String, Object> orderRequest) {
        logger.info("Order creation initiated");
        
        // Validate order request
        if (orderRequest == null || !orderRequest.containsKey("items")) {
            throw new BusinessException("INVALID_ORDER", "Order request must contain 'items' field");
        }
        
        // Extract items from order request
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> items = (List<Map<String, Object>>) orderRequest.get("items");
        
        // Validate items
        if (items == null || items.isEmpty()) {
            throw new BusinessException("EMPTY_ORDER", "Cannot create order with no items");
        }
        
        logger.debug("Processing order for {} items", items.size());
        
        // Reserve inventory using Feign client
        // If inventory service is unavailable, FeignException will be thrown
        // GlobalExceptionHandler will catch and handle it automatically
        InventoryRequest inventoryRequest = new InventoryRequest(items);
        InventoryResponse inventoryResponse = inventoryServiceClient.reserveItems(inventoryRequest);
        
        // Generate order ID
        String orderId = UUID.randomUUID().toString();
        
        // Build success response
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now().toString());
        response.put("status", HttpStatus.OK.value());
        response.put("message", "Order created successfully");
        
        Map<String, Object> data = new HashMap<>();
        data.put("orderId", orderId);
        data.put("service", "order-service");
        data.put("inventoryResponse", inventoryResponse);
        response.put("data", data);
        
        logger.info("Order created successfully: {}", orderId);
        return ResponseEntity.ok(response);
    }
}
