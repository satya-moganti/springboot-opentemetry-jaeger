package com.jayam.cartservice.config;

import java.util.concurrent.TimeUnit;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import feign.Logger;
import feign.Request;
import feign.Retryer;

/**
 * Feign Client Configuration
 * 
 * Note: Trace context propagation is automatically handled by Spring Boot's
 * auto-configuration when micrometer-tracing-bridge-otel is on the classpath.
 * 
 * Spring Boot automatically configures:
 * - TracingFeignClient (wraps Feign clients with tracing)
 * - Propagates trace headers (traceparent, tracestate) via W3C propagation
 * - Integrates with Micrometer ObservationRegistry
 */
@Configuration
public class FeignConfig {
    
    @Bean
    public Request.Options requestOptions() {
        // Connect timeout: 5 seconds, Read timeout: 10 seconds
        return new Request.Options(
            5, TimeUnit.SECONDS,
            10, TimeUnit.SECONDS,
            true
        );
    }
    
    @Bean
    public Retryer retryer() {
        // No retries by default (can be customized)
        return Retryer.NEVER_RETRY;
    }
    
    /**
     * Configure Feign logging level
     * NONE: No logging (default)
     * BASIC: Log only request method and URL and response status and execution time
     * HEADERS: Log basic information plus request and response headers
     * FULL: Log headers, body, and metadata for both request and response
     */
    @Bean
    public Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;
    }
}
