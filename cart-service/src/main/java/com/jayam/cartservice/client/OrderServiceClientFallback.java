package com.jayam.cartservice.client;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.jayam.cartservice.dto.OrderRequest;
import com.jayam.cartservice.dto.OrderResponse;

/**
 * Fallback implementation for OrderServiceClient
 * This is invoked when the circuit breaker is open or the service is unavailable
 */
@Component
public class OrderServiceClientFallback implements OrderServiceClient {

    @Override
    public OrderResponse createOrder(OrderRequest request) {
        // Return a fallback response when order service is unavailable
        OrderResponse fallbackResponse = new OrderResponse();
        fallbackResponse.setTimestamp(LocalDateTime.now().toString());
        fallbackResponse.setStatus(503);
        fallbackResponse.setCode(503);
        fallbackResponse.setMessage("Order service is temporarily unavailable. Your order has been queued.");
        
        Map<String, Object> data = new HashMap<>();
        data.put("service", "order-service-fallback");
        data.put("fallback", true);
        data.put("reason", "Circuit breaker is OPEN or service unavailable");
        data.put("action", "Order will be processed when service is back online");
        
        fallbackResponse.setData(data);
        
        return fallbackResponse;
    }
}
