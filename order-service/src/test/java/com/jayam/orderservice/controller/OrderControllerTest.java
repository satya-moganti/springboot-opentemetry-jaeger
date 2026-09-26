package com.jayam.orderservice.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayam.orderservice.client.InventoryServiceClient;
import com.jayam.orderservice.dto.InventoryRequest;
import com.jayam.orderservice.dto.InventoryResponse;

import feign.FeignException;
import feign.Request;
import feign.RequestTemplate;

@WebMvcTest(OrderController.class)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private InventoryServiceClient inventoryServiceClient;

    @Test
    void createOrder_Success() throws Exception {
        // Arrange
        Map<String, Object> orderRequest = new HashMap<>();
        orderRequest.put("items", java.util.List.of(
            Map.of("id", "product-1", "qty", 2)
        ));

        InventoryResponse mockInventoryResponse = new InventoryResponse();
        mockInventoryResponse.setStatus(200);
        mockInventoryResponse.setMessage("Items reserved successfully");
        Map<String, Object> inventoryData = new HashMap<>();
        inventoryData.put("reservedItems", java.util.List.of(Map.of("id", "product-1", "qty", 2)));
        mockInventoryResponse.setData(inventoryData);

        when(inventoryServiceClient.reserveItems(any(InventoryRequest.class)))
            .thenReturn(mockInventoryResponse);

        // Act & Assert
        mockMvc.perform(post("/orders/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(orderRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Order created successfully"))
                .andExpect(jsonPath("$.data.service").value("order-service"))
                .andExpect(jsonPath("$.data.orderId").exists())
                .andExpect(jsonPath("$.data.inventoryResponse.status").value(200));
    }

    @Test
    void createOrder_InventoryServiceUnavailable() throws Exception {
        // Arrange
        Map<String, Object> orderRequest = new HashMap<>();
        orderRequest.put("items", java.util.List.of(
            Map.of("id", "product-1", "qty", 2)
        ));

        // Simulate service unavailable
        Request request = Request.create(Request.HttpMethod.POST, "/inventory/reserve", 
            Map.of(), null, new RequestTemplate());
        when(inventoryServiceClient.reserveItems(any(InventoryRequest.class)))
            .thenThrow(new FeignException.ServiceUnavailable(
                "Service unavailable", request, null, null));

        // Act & Assert
        mockMvc.perform(post("/orders/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(orderRequest)))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.status").value(503))
                .andExpect(jsonPath("$.message").value("Inventory service is currently unavailable"))
                .andExpect(jsonPath("$.data.service").value("order-service"));
    }

    @Test
    void createOrder_FallbackTriggered() throws Exception {
        // Test when circuit breaker is open and fallback is used
        Map<String, Object> orderRequest = new HashMap<>();
        orderRequest.put("items", java.util.List.of(
            Map.of("id", "product-1", "qty", 2)
        ));

        // Simulate fallback response
        InventoryResponse fallbackResponse = new InventoryResponse();
        fallbackResponse.setStatus(503);
        fallbackResponse.setMessage("Inventory service is temporarily unavailable");
        Map<String, Object> fallbackData = new HashMap<>();
        fallbackData.put("fallback", true);
        fallbackResponse.setData(fallbackData);

        when(inventoryServiceClient.reserveItems(any(InventoryRequest.class)))
            .thenReturn(fallbackResponse);

        // Act & Assert
        mockMvc.perform(post("/orders/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(orderRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.inventoryResponse.data.fallback").value(true));
    }
}
