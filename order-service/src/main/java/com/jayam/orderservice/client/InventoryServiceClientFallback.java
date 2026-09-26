package com.jayam.orderservice.client;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.jayam.orderservice.dto.InventoryRequest;
import com.jayam.orderservice.dto.InventoryResponse;

/**
 * Fallback implementation for InventoryServiceClient
 * This is invoked when the circuit breaker is open or the service is unavailable
 */
@Component
public class InventoryServiceClientFallback implements InventoryServiceClient {

    @Override
    public InventoryResponse reserveItems(InventoryRequest request) {
        // Return a fallback response when inventory service is unavailable
        InventoryResponse fallbackResponse = new InventoryResponse();
        fallbackResponse.setTimestamp(LocalDateTime.now().toString());
        fallbackResponse.setStatus(503);
        fallbackResponse.setCode(503);
        fallbackResponse.setMessage("Inventory service is temporarily unavailable. Reservation pending.");
        
        Map<String, Object> data = new HashMap<>();
        data.put("service", "inventory-service-fallback");
        data.put("fallback", true);
        data.put("reason", "Circuit breaker is OPEN or service unavailable");
        data.put("action", "Items will be reserved when service is back online");
        
        fallbackResponse.setData(data);
        
        return fallbackResponse;
    }
}
