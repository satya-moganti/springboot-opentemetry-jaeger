package com.jayam.orderservice.dto;

import java.util.List;
import java.util.Map;

public class InventoryRequest {
    private List<Map<String, Object>> items;

    public InventoryRequest() {
    }

    public InventoryRequest(List<Map<String, Object>> items) {
        this.items = items;
    }

    public List<Map<String, Object>> getItems() {
        return items;
    }

    public void setItems(List<Map<String, Object>> items) {
        this.items = items;
    }
}
