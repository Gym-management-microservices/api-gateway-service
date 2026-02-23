package micro.gym.apigatewayservice.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                // Classes Management Service
                .route("classes-service", r -> r
                        .path("/api/classes/**")
                        .filters(f -> f.rewritePath("/api/classes(?<segment>/?.*)", "/classes${segment}"))
                        .uri("http://localhost:8081"))
                
                // Equipment Management Service
                .route("equipment-service", r -> r
                        .path("/api/equipment/**")
                        .filters(f -> f.rewritePath("/api/equipment(?<segment>/?.*)", "/equipment${segment}"))
                        .uri("http://localhost:8082"))
                
                // Members Management Service
                .route("members-service", r -> r
                        .path("/api/members/**")
                        .filters(f -> f.rewritePath("/api/members(?<segment>/?.*)", "/members${segment}"))
                        .uri("http://localhost:8083"))
                
                // Trainers Management Service
                .route("trainers-service", r -> r
                        .path("/api/trainers/**")
                        .filters(f -> f.rewritePath("/api/trainers(?<segment>/?.*)", "/trainer${segment}"))
                        .uri("http://localhost:8100"))
                
                .build();
    }
}
