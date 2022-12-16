package com.microservice.apigateway.config;

import com.microservice.apigateway.filter.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class GatewayConfig {

    private final JwtAuthenticationFilter filter;

    @Bean
    public RouteLocator routes(RouteLocatorBuilder builder) {
        return builder.routes().route("AUTH-SERVICE", r -> r.path("/authenticate/**").filters(f -> f.filter(filter)).uri("lb://AUTH-SERVICE"))
                .route("PRODUCT-SERVICE", r -> r.path("/product/**").filters(f -> f.filter(filter)).uri("lb://PRODUCT-SERVICE"))
                .route("PAYMENT-SERVICE", r -> r.path("/payment/**").filters(f -> f.filter(filter)).uri("lb://PAYMENT-SERVICE"))
                .route("ORDER-SERVICE", r -> r.path("/order/**").filters(f -> f.filter(filter)).uri("lb://ORDER-SERVICE")).build();
    }
}
