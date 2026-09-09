package com.orbit.gateway.controller;

import com.orbit.gateway.demo.DemoModeState;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/demo/rate-limit/mode")
public class DemoModeController {

    private final DemoModeState demoModeState;

    public DemoModeController(DemoModeState demoModeState) {
        this.demoModeState = demoModeState;
    }

    @GetMapping
    public Map<String, String> getMode() {
        return Map.of("mode", demoModeState.isRateLimitEnabled() ? "IMPROVED" : "NAIVE");
    }

    @PostMapping("/naive")
    public Map<String, String> enableNaive() {
        demoModeState.setRateLimitEnabled(false);
        return Map.of("mode", "NAIVE", "description", "No rate limiting - unlimited requests pass through");
    }

    @PostMapping("/improved")
    public Map<String, String> enableImproved() {
        demoModeState.setRateLimitEnabled(true);
        return Map.of("mode", "IMPROVED", "description", "Token Bucket rate limiting active");
    }
}

/**
 *  pull the postgres image : docker pull <imagename>
 *  created and run the container: docker run --name <container-name>
 *                                 -e <environment-var> -p <port-mapping>
 *                                 --network <network-name> <image-name>
 *
 *  to access the container : docker exec -it <container-name> bash
 *  Create Dockerfile for our microservice
 *  Created docker image of our microservice: docker build -t <image-name> .
 *  Created and run the container for microservice: docker run --name <container-name>
 *  *                                  -p <port-mapping> --network <network-name> <image-name>
 *
 *  docker compose: everything can be containerized at once
 *                  docker-compose up -d
 *
 *  Mono Repo - same repository
 *  Multi Repo -
 */
