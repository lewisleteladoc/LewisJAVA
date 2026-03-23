package com.example.demo;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import com.example.demo.BusinessObjects.Food;
import com.example.demo.BusinessObjects.Meal;
import com.example.demo.fileLoader.FoodParser;
import com.example.demo.DataSource.MemberMealStore;

@RestController
@RequestMapping("/api/food")
public class UsdaFoodController {

    // Request body class
    private static class AddFoodRequest {
        private String type;
        private String[] fdcIds;
    
        // Getters and setters
        public String getType() { return type; }            
        public String[] getFdcIds() { return fdcIds; }        
    }

     // ── Request body: add member ──────────────────────────────────────────────
    // private static class AddMemberRequest {
    //     @NotBlank(message = "name is required")
    //     private String name;

    //     @NotNull(message = "age is required")
    //     @Min(value = 0, message = "age must be 0 or greater")
    //     private Integer age;

    //     public String getName()  { return name; }
    //     public Integer getAge()  { return age; }
    // }

    @Value("${USDAapiKey}")
    private String apiKey;

    @Value("${USDAapiUrl}")
    private String apiUrl;

    private final TokenService tokenService;
    private final WebClient webClient;
    private final FoodParser foodParser;
    private final MemberMealStore memberMealStore;

    public UsdaFoodController(TokenService tokenService, WebClient webClient, FoodParser foodParser, MemberMealStore memberMealStore) {
        this.tokenService = tokenService;
        this.webClient = webClient;
        this.foodParser = foodParser;
        this.memberMealStore = memberMealStore;
    }    
    

    @GetMapping("/search")
    public ResponseEntity<List<Food>> getFood(
            HttpServletRequest request,
            @RequestParam String query
    ) {
        List<Food> foodList = foodParser.getFoodList().stream()
                .filter(food -> food.getName().toLowerCase().contains(query.toLowerCase()))
                .limit(20)
                .toList();
        
        return foodList.isEmpty()
                ? ResponseEntity.status(HttpStatus.NO_CONTENT).build()
                : ResponseEntity.ok(foodList);
    }

    @GetMapping("/foodlist")
    public ResponseEntity<List<Food>> getFoodList(
            HttpServletRequest request
    ) {
        List<Food> foodList = foodParser.getFoodList();
        
        return foodList.isEmpty()
                ? ResponseEntity.status(HttpStatus.NO_CONTENT).build()
                : ResponseEntity.ok(foodList);
    }

    @GetMapping("/{fdcId}")
    public ResponseEntity<String> getFoodById(
            @PathVariable String fdcId,
            HttpServletRequest request
    ) {
        // 1. Extract token from header
        String authHeader = request.getHeader("Authorization");        
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

        if (scopes.contains("Invalid Token") || !scopes.contains("ReadUser")) {
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

    @GetMapping("/getmembermeals")
    public ResponseEntity<?> getMemberMeals(            
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

        if (scopes.contains("Invalid Token") || !scopes.contains("ReadUser")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Unauthorized: invalid token.");
        }

        // 3. Call USDA API
        try {
           List<Meal> result = memberMealStore.getMeals(username);

            return ResponseEntity.ok(result);

        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                    .body("Upstream service error.");
        }
    }

    @GetMapping("/getmealtypes")
    public ResponseEntity<?> getMealTypes(    
            @RequestParam String mealType,        
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

        if (scopes.contains("Invalid Token") || !scopes.contains("ReadUser")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Unauthorized: invalid token.");
        }

        // 3. Call USDA API
        try {
           List<Meal> result = memberMealStore.getMealTypes(username, mealType);

            return ResponseEntity.ok(result);

        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                    .body("Upstream service error.");
        }
    }
    
    // Controller method
    @PostMapping("/addmeal")
    public ResponseEntity<String> addFood(@Valid @RequestBody AddFoodRequest request,
        HttpServletRequest headerRequest
    ) {
        try {
             // 1. Extract token from header
            String authHeader = headerRequest.getHeader("Authorization");
            String username = headerRequest.getHeader("X-username"); // Optional: for logging or additional checks
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

            if (scopes.contains("Invalid Token") || !scopes.contains("WriteUser")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Unauthorized: invalid token.");
            }
            String[] fdcIds = request.getFdcIds();
            String type = request.getType();
    
            List<Food> existingFoods = foodParser.getFoodsByFdcIdsFoods(fdcIds);
            if (existingFoods.size() > 0) {
                // Some foods found in local list
                Meal meal = new Meal(type, existingFoods);
                memberMealStore.addMeal(username, meal); // Example member ID
                return ResponseEntity.ok("Meal added successfully.");
            }
            
            return ResponseEntity.ok("Some foods not found.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred while processing the request.");
        }
    }
}