package com.jayam.inventoryservice.controller;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jayam.inventoryservice.exception.BusinessException;
import com.jayam.inventoryservice.exception.InsufficientStockException;

/**
 * Inventory Controller - Handles inventory management and reservation
 * 
 * Exception handling is managed by GlobalExceptionHandler
 * This controller throws exceptions which are automatically caught and formatted
 */
@RestController
@RequestMapping("/inventory")
public class InventoryController {
    
    private static final Logger logger = LoggerFactory.getLogger(InventoryController.class);
    private final Random random = new Random();

    /**
     * Reserve items endpoint - Reserves inventory for order fulfillment
     * 
     * @param inventoryRequest Map containing items to reserve
     * @return ResponseEntity with reservation result
     * @throws BusinessException if request is invalid
     * @throws InsufficientStockException if stock is insufficient for any item
     */
    @PostMapping("/reserve")
    public ResponseEntity<Map<String, Object>> reserveItems(@RequestBody Map<String, Object> inventoryRequest) {
        logger.info("Inventory reservation initiated");
        
        // Validate request
        if (inventoryRequest == null || !inventoryRequest.containsKey("items")) {
            throw new BusinessException("INVALID_REQUEST", "Inventory request must contain 'items' field");
        }
        
        // Extract items
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> items = (List<Map<String, Object>>) inventoryRequest.get("items");
        
        // Validate items
        if (items == null || items.isEmpty()) {
            throw new BusinessException("EMPTY_REQUEST", "Cannot reserve zero items");
        }
        
        logger.debug("Processing reservation for {} items", items.size());
        
        // Simulate stock checking (in real scenario, query database)
        // For demonstration, randomly simulate insufficient stock
        if (random.nextInt(100) < 10) { // 10% chance of insufficient stock
            String itemId = items.get(0).get("itemId") != null ? 
                items.get(0).get("itemId").toString() : "ITEM-" + random.nextInt(1000);
            int requestedQty = 5;
            int availableQty = 2;
            
            logger.warn("Insufficient stock for item: {}", itemId);
            throw new InsufficientStockException(itemId, requestedQty, availableQty);
        }
        
        // Build success response
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now().toString());
        response.put("status", HttpStatus.OK.value());
        response.put("message", "Items reserved successfully");
        
        Map<String, Object> data = new HashMap<>();
        data.put("service", "inventory-service");
        data.put("reservedItems", items);
        data.put("reservationId", "RES-" + System.currentTimeMillis());
        response.put("data", data);
        
        logger.info("Inventory reserved successfully for {} items", items.size());
        return ResponseEntity.ok(response);
    }
}
