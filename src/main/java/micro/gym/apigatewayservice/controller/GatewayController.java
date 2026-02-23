package micro.gym.apigatewayservice.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/info")
public class GatewayController {

    @GetMapping("/services")
    public Map<String, Object> getServicesInfo() {
        Map<String, Object> info = new HashMap<>();
        
        Map<String, String> services = new HashMap<>();
        services.put("classes", "http://localhost:8080/api/classes");
        services.put("equipment", "http://localhost:8080/api/equipment");
        services.put("members", "http://localhost:8080/api/members");
        services.put("trainers", "http://localhost:8080/api/trainers");
        
        info.put("gateway_port", "8080");
        info.put("available_services", services);
        info.put("status", "running");
        
        return info;
    }

    @GetMapping("/health")
    public Map<String, String> health() {
        Map<String, String> health = new HashMap<>();
        health.put("status", "UP");
        health.put("service", "API Gateway");
        return health;
    }
}
