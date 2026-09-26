package com.jayam.cartservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.jayam.cartservice.dto.OrderRequest;
import com.jayam.cartservice.dto.OrderResponse;

@FeignClient(
    name = "order-service",
    fallback = OrderServiceClientFallback.class
)
public interface OrderServiceClient {
    
    @PostMapping("/orders/create")
    OrderResponse createOrder(@RequestBody OrderRequest request);
}
