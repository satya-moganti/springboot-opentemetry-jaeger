package com.jayam.orderservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.jayam.orderservice.dto.InventoryRequest;
import com.jayam.orderservice.dto.InventoryResponse;

@FeignClient(
    name = "inventory-service",
    fallback = InventoryServiceClientFallback.class
)
public interface InventoryServiceClient {
    
    @PostMapping("/inventory/reserve")
    InventoryResponse reserveItems(@RequestBody InventoryRequest request);
}
