package com.jayam.cartservice.dto;

import java.util.List;
import java.util.Map;

public class OrderRequest {
    private List<Map<String, Object>> items;

    public OrderRequest() {
    }

    public OrderRequest(List<Map<String, Object>> items) {
        this.items = items;
    }

    public List<Map<String, Object>> getItems() {
        return items;
    }

    public void setItems(List<Map<String, Object>> items) {
        this.items = items;
    }
}
