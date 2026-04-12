package micro.gym.apigatewayservice.config;

import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;

@Configuration
public class GatewayConfig {

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder, AggregationFilter aggregationFilter) {
        return builder.routes()
                // Classes Management Service
                .route("classes-service", r -> r
                        .path("/api/classes/**")
                        .filters(f -> f.rewritePath("/api/classes(?<segment>/?.*)", "/classes${segment}"))
                        .uri("lb://classes-service"))

                .route("equipment-service", r -> r
                        .path("/api/equipment/**")
                        .filters(f -> f.rewritePath("/api/equipment(?<segment>/?.*)", "/equipment${segment}"))
                        .uri("lb://equipment-service"))

                .route("members-service", r -> r
                        .path("/api/members/**")
                        .filters(f -> f.rewritePath("/api/members(?<segment>/?.*)", "/members${segment}"))
                        .uri("lb://members-service"))

                .route("trainers-service", r -> r
                        .path("/api/trainers/**")
                        .filters(f -> f.rewritePath("/api/trainers(?<segment>/?.*)", "/trainers${segment}"))
                        .uri("lb://trainers-service"))

                .route("aggregated-info", r -> r
                        .path("/api/aggregated-info")
                        .filters(f -> f.filter(aggregationFilter))
                        .uri("no://op"))

                        .build();
    }

    @Bean
    public GlobalFilter customGlobalFilter() {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            String authHeader =
                    request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
            if (authHeader != null) {
                return chain.filter(exchange.mutate()
                        .request(request.mutate()
                                .header(HttpHeaders.AUTHORIZATION, authHeader)
                                .build())
                        .build());
            }
            return chain.filter(exchange);
        };
    }
}
