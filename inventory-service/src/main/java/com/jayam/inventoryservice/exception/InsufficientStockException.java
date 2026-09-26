package com.jayam.inventoryservice.exception;

/**
 * Exception thrown when inventory stock is insufficient
 */
public class InsufficientStockException extends BusinessException {
    
    private final String itemId;
    private final int requestedQuantity;
    private final int availableQuantity;
    
    public InsufficientStockException(String itemId, int requestedQuantity, int availableQuantity) {
        super("INSUFFICIENT_STOCK",
            String.format("Insufficient stock for item '%s'. Requested: %d, Available: %d",
                itemId, requestedQuantity, availableQuantity));
        this.itemId = itemId;
        this.requestedQuantity = requestedQuantity;
        this.availableQuantity = availableQuantity;
    }
    
    public String getItemId() {
        return itemId;
    }
    
    public int getRequestedQuantity() {
        return requestedQuantity;
    }
    
    public int getAvailableQuantity() {
        return availableQuantity;
    }
}
