package micro.gym.apigatewayservice.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import com.fasterxml.jackson.databind.ObjectMapper;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@Component
public class AggregationFilter implements GatewayFilter {

        @Autowired
        private WebClient.Builder webClientBuilder;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        if (exchange.getRequest().getPath().value().equals("/api/aggregated-info")) {
            return aggregateGymResponses(exchange);
        }

        return chain.filter(exchange);
    }

    private Mono<Void> aggregateGymResponses(ServerWebExchange exchange) {
        String authHeader = exchange.getRequest().getHeaders()
                .getFirst("Authorization");

        Mono<String> classesInfo = webClientBuilder.build().get()
                .uri("http://classes-service/classes")
                .header("Authorization", authHeader)
                .retrieve()
                .bodyToMono(String.class)
                .onErrorReturn("[]");

        Mono<String> trainersInfo = webClientBuilder.build().get()
                .uri("http://trainers-service/trainers")
                .header("Authorization", authHeader)
                .retrieve()
                .bodyToMono(String.class)
                .onErrorReturn("[]");

        return Mono.zip(classesInfo, trainersInfo)
                .flatMap(tuple -> {

                    Map<String, Object> result = new HashMap<>();
                    result.put("classes", tuple.getT1());
                    result.put("trainers", tuple.getT2());

                    exchange.getResponse().setStatusCode(HttpStatus.OK);
                    exchange.getResponse().getHeaders()
                            .setContentType(MediaType.APPLICATION_JSON);

                    try {
                        byte[] bytes = new ObjectMapper()
                                .writeValueAsBytes(result);
                        return exchange.getResponse()
                                .writeWith(Mono.just(exchange.getResponse()
                                        .bufferFactory().wrap(bytes)));
                    } catch (Exception e) {
                        exchange.getResponse()
                                .setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
                        return exchange.getResponse().setComplete();
                    }
                });
    }
}