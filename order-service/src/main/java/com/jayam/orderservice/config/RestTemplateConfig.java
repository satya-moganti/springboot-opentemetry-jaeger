package com.jayam.orderservice.config;

import java.time.Duration;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * RestTemplateConfig - DEPRECATED
 * 
 * This configuration has been replaced with FeignClient.
 * Kept for reference only. Can be removed if no other services need RestTemplate.
 */
@Configuration
public class RestTemplateConfig {
    
    // Commented out - now using FeignClient instead
    // Uncomment if you need RestTemplate for other purposes
    /*
    @Bean
    @LoadBalanced
    RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder
                .connectTimeout(Duration.ofSeconds(5))
                .readTimeout(Duration.ofSeconds(10))
                .build();
    }
    */
}
