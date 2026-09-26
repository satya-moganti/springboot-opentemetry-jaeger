package com.jayam.cartservice.exception;

/**
 * Exception thrown when a downstream service is unavailable
 */
public class ServiceUnavailableException extends RuntimeException {
    
    private final String serviceName;
    
    public ServiceUnavailableException(String serviceName, String message) {
        super(message);
        this.serviceName = serviceName;
    }
    
    public ServiceUnavailableException(String serviceName, String message, Throwable cause) {
        super(message, cause);
        this.serviceName = serviceName;
    }
    
    public String getServiceName() {
        return serviceName;
    }
}
