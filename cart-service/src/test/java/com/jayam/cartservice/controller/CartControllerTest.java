package com.jayam.cartservice.controller;

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
import com.jayam.cartservice.client.OrderServiceClient;
import com.jayam.cartservice.dto.OrderRequest;
import com.jayam.cartservice.dto.OrderResponse;

import feign.FeignException;
import feign.Request;
import feign.RequestTemplate;

@WebMvcTest(CartController.class)
class CartControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private OrderServiceClient orderServiceClient;

    @Test
    void checkout_Success() throws Exception {
        // Arrange
        Map<String, Object> cartRequest = new HashMap<>();
        cartRequest.put("items", java.util.List.of(
            Map.of("id", "product-1", "qty", 2)
        ));

        OrderResponse mockOrderResponse = new OrderResponse();
        mockOrderResponse.setStatus(200);
        mockOrderResponse.setMessage("Order created successfully");
        Map<String, Object> orderData = new HashMap<>();
        orderData.put("orderId", "test-order-123");
        mockOrderResponse.setData(orderData);

        when(orderServiceClient.createOrder(any(OrderRequest.class)))
            .thenReturn(mockOrderResponse);

        // Act & Assert
        mockMvc.perform(post("/cart/checkout")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cartRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Checkout initiated"))
                .andExpect(jsonPath("$.data.service").value("cart-service"))
                .andExpect(jsonPath("$.data.orderResponse.status").value(200));
    }

    @Test
    void checkout_OrderServiceUnavailable() throws Exception {
        // Arrange
        Map<String, Object> cartRequest = new HashMap<>();
        cartRequest.put("items", java.util.List.of(
            Map.of("id", "product-1", "qty", 2)
        ));

        // Simulate service unavailable
        Request request = Request.create(Request.HttpMethod.POST, "/orders/create", 
            Map.of(), null, new RequestTemplate());
        when(orderServiceClient.createOrder(any(OrderRequest.class)))
            .thenThrow(new FeignException.ServiceUnavailable(
                "Service unavailable", request, null, null));

        // Act & Assert
        mockMvc.perform(post("/cart/checkout")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cartRequest)))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.status").value(503))
                .andExpect(jsonPath("$.message").value("Order service is currently unavailable"))
                .andExpect(jsonPath("$.data.service").value("cart-service"));
    }

    @Test
    void checkout_FallbackTriggered() throws Exception {
        // Test when circuit breaker is open and fallback is used
        Map<String, Object> cartRequest = new HashMap<>();
        cartRequest.put("items", java.util.List.of(
            Map.of("id", "product-1", "qty", 2)
        ));

        // Simulate fallback response
        OrderResponse fallbackResponse = new OrderResponse();
        fallbackResponse.setStatus(503);
        fallbackResponse.setMessage("Order service is temporarily unavailable");
        Map<String, Object> fallbackData = new HashMap<>();
        fallbackData.put("fallback", true);
        fallbackResponse.setData(fallbackData);

        when(orderServiceClient.createOrder(any(OrderRequest.class)))
            .thenReturn(fallbackResponse);

        // Act & Assert
        mockMvc.perform(post("/cart/checkout")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cartRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.orderResponse.data.fallback").value(true));
    }
}
