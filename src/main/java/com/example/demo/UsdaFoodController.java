package com.example.demo;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/food")
public class UsdaFoodController {

    @Value("${USDAapiKey}")
    private String apiKey;

    @Value("${USDAapiUrl}")
    private String apiUrl;

    private final TokenService tokenService;
    private final WebClient webClient;

    public UsdaFoodController(TokenService tokenService, WebClient webClient) {
        this.tokenService = tokenService;
        this.webClient = webClient;
    }

    @GetMapping("/{fdcId}")
    public ResponseEntity<String> getFoodById(
            @PathVariable String fdcId,
            HttpServletRequest request
    ) {
        // 1. Extract token from header
        String authHeader = request.getHeader("Authorization");
        String username = request.getHeader("X-username"); // Optional: for logging or additional checks
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Unauthorized: missing or malformed Authorization header.");
        }
        String token = authHeader.substring(7);

        // 2. Validate token
        List<String> scopes;
        try {
            scopes = tokenService.getScopes(token);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Unauthorized: " + e.getMessage());
        }

        if (scopes.contains("Invalid Token")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Unauthorized: invalid token.");
        }

        // 3. Call USDA API
        try {
            String fullUrl = String.format("%s/fdc/v1/food/%s?format=abridged&api_key=%s",
                    apiUrl, fdcId, apiKey);

            String result = webClient.get()
                    .uri(fullUrl)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            return ResponseEntity.ok(result);

        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                    .body("Upstream service error.");
        }
    }
}