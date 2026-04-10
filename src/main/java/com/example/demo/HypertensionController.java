package com.example.demo;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.List;

import java.util.Arrays;

@RestController
@RequestMapping("/api/food")
public class HypertensionApplication {

    // Request body class
    private static class AddFoodRequest {
        private String type;
        private String[] fdcIds;
    
        // Getters and setters
        public String getType() { return type; }            
        public String[] getFdcIds() { return fdcIds; }        
    }

    private static class RemoveFoodRequest {
        private String mealId;
        private String[] fdcIds;
    
        // Getters and setters
        public String getMealId() { return mealId; }            
        public String[] getFdcIds() { return fdcIds; }        
    }

    // private final UsdaFoodSearch usdaFoodSearch;
     
    @Value("${USDAapiKey}")
    private String apiKey;

    @Value("${USDAapiUrl}")
    private String apiUrl;

    //  @Value("${NinjaApiKey}")
    // private String ninjaApiKey;

    // @Value("${NinjaApiUrl}")
    // private String ninjaApiUrl;

    private final TokenService tokenService;
    private final WebClient webClient;
    // private final FoodParser foodParser;
    // private final MemberMealStore memberMealStore;

    public HypertensionApplication(TokenService tokenService, WebClient webClient) {
        this.tokenService = tokenService;
        this.webClient = webClient;        
    }    
    
    @GetMapping("/search")
    public ResponseEntity<?> getFood(
            HttpServletRequest request,
            @RequestParam String query
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
        
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Unauthorized: invalid token.");
        
    }

    // @GetMapping("/search2")
    // public ResponseEntity<?> getFood2(
    //         HttpServletRequest request,
    //         @RequestParam String query
    // ) {
    //     // 1. Extract token from header
    //     String authHeader = request.getHeader("Authorization");        
    //     if (authHeader == null || !authHeader.startsWith("Bearer ")) {
    //         return 
    //             ResponseEntity.status(HttpStatus.UNAUTHORIZED)
    //                 .body("Unauthorized: missing or malformed Authorization header.");
    //     }
    //     String token = authHeader.substring(7);

    //     // 2. Validate token
    //     List<String> scopes;
    //     try {
    //         scopes = tokenService.getScopes(token);
    //     } catch (Exception e) {            
    //             return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
    //                 .body("Unauthorized: " + e.getMessage());
    //     }

    //     if (scopes != null && (scopes.contains("Invalid Token") || !scopes.contains("ReadUser"))) {            
    //             return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
    //                 .body("Unauthorized: invalid token.");
    //     }
        
    //     // 3. Check local data first
    //     List<Food> foodList = foodParser.getFoodList()
    //                             .stream()
    //                             .filter(food -> food.getCalories() > 0 && food.getName().toLowerCase().contains(query.toLowerCase()))
    //                             .limit(20)
    //                             .toList();
        
    //     // 4. Logic: If found locally, return immediately. If not, call USDA.
    //     if (!foodList.isEmpty()) {
    //         return ResponseEntity.ok(foodList);
    //     }

    //     // Call the Async USDA service and transform the result into a ResponseEntity
    //     try {
    //         List<Food> returnValue = usdaFoodSearch.search(query);
    //         return ResponseEntity.ok(returnValue);
    //     } catch (Exception ex) {
    //         return ResponseEntity.status(HttpStatus.NOT_FOUND)
    //                 .body("query: " + query + " not found.");
    //     }        
    // }

    // @GetMapping("/foodlist")
    // public ResponseEntity<?> getFoodList(
    //         HttpServletRequest request
    // ) {
    //     // 1. Extract token from header
    //     String authHeader = request.getHeader("Authorization");        
    //     if (authHeader == null || !authHeader.startsWith("Bearer ")) {
    //         return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
    //                 .body("Unauthorized: missing or malformed Authorization header.");
    //     }
    //     String token = authHeader.substring(7);

    //     // 2. Validate token
    //     List<String> scopes;
    //     try {
    //         scopes = tokenService.getScopes(token);
    //     } catch (Exception e) {
    //         return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
    //                 .body("Unauthorized: " + e.getMessage());
    //     }

    //     if (scopes.contains("Invalid Token") || !scopes.contains("ReadUser")) {
    //         return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
    //                 .body("Unauthorized: invalid token.");
    //     }

    //     List<Food> foodList = foodParser.getFoodList();
        
    //     return foodList.isEmpty()
    //             ? ResponseEntity.status(HttpStatus.NO_CONTENT).build()
    //             : ResponseEntity.ok(foodList);
    // }

    // @GetMapping("/{fdcId}")
    // public ResponseEntity<String> getFoodById(
    //         @PathVariable String fdcId,
    //         HttpServletRequest request
    // ) {
    //     // 1. Extract token from header
    //     String authHeader = request.getHeader("Authorization");        
    //     if (authHeader == null || !authHeader.startsWith("Bearer ")) {
    //         return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
    //                 .body("Unauthorized: missing or malformed Authorization header.");
    //     }
    //     String token = authHeader.substring(7);

    //     // 2. Validate token
    //     List<String> scopes;
    //     try {
    //         scopes = tokenService.getScopes(token);
    //     } catch (Exception e) {
    //         return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
    //                 .body("Unauthorized: " + e.getMessage());
    //     }

    //     if (scopes.contains("Invalid Token") || !scopes.contains("ReadUser")) {
    //         return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
    //                 .body("Unauthorized: invalid token.");
    //     }

    //     // 3. Call USDA API
    //     try {
    //         String fullUrl = String.format("%s/fdc/v1/food/%s?format=abridged&api_key=%s",
    //                 apiUrl, fdcId, apiKey);

    //         String result = webClient.get()
    //                 .uri(fullUrl)
    //                 .retrieve()
    //                 .bodyToMono(String.class)
    //                 .block();

    //         return ResponseEntity.ok(result);

    //     } catch (Exception ex) {
    //         return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
    //                 .body("Upstream service error.");
    //     }
    // }

    
    // @GetMapping("/getmembermeals")
    // public ResponseEntity<?> getMemberMeals(            
    //         HttpServletRequest request
    // ) {
    //     // 1. Extract token from header
    //     String authHeader = request.getHeader("Authorization");       
    //     String username = request.getHeader("X-username"); // Optional: for logging or additional checks 
    //     if (authHeader == null || !authHeader.startsWith("Bearer ")) {
    //         return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
    //                 .body("Unauthorized: missing or malformed Authorization header.");
    //     }
    //     String token = authHeader.substring(7);

    //     // 2. Validate token
    //     List<String> scopes;
    //      String sub = null;
    //     try {
    //         sub = tokenService.getSub(token);
    //         scopes = tokenService.getScopes(token);
    //     } catch (Exception e) {
    //         return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
    //                 .body("Unauthorized: " + e.getMessage());
    //     }

    //     if (scopes.contains("Invalid Token") || !scopes.contains("ReadUser")) {
    //         return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
    //                 .body("Unauthorized: invalid token.");
    //     }

    //     // 3. Call USDA API
    //     try {
    //        List<Meal> result = memberMealStore.getMeals(sub != null ? sub : username);

    //         return ResponseEntity.ok(result);

    //     } catch (Exception ex) {
    //         return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
    //                 .body("Upstream service error.");
    //     }
    // }

    // // getmemberlatestnumberofmeals
    // @GetMapping("/getmemberlatestnumberofmeals")
    // public ResponseEntity<?> getMemberLatestNumberOfMeals(    
    //         int start,
    //         int count,
    //         HttpServletRequest request
    // ) {
    //     // 1. Extract token from header
    //     String authHeader = request.getHeader("Authorization");       
    //     String username = request.getHeader("X-username"); // Optional: for logging or additional checks 
    //     if (authHeader == null || !authHeader.startsWith("Bearer ")) {
    //         return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
    //                 .body("Unauthorized: missing or malformed Authorization header.");
    //     }
    //     String token = authHeader.substring(7);

    //     // 2. Validate token
    //     List<String> scopes;
    //     String sub = null;
    //     try {
    //         sub = tokenService.getSub(token);
    //         scopes = tokenService.getScopes(token);
    //     } catch (Exception e) {
    //         return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
    //                 .body("Unauthorized: " + e.getMessage());
    //     }

    //     if (scopes.contains("Invalid Token") || !scopes.contains("ReadUser")) {
    //         return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
    //                 .body("Unauthorized: invalid token.");
    //     }

    //     // 3. Call USDA API
    //     try {
    //        List<Meal> result = memberMealStore.getmemberlatestnumberofmeals(sub != null ? sub : username, start, count);

    //        return ResponseEntity.ok(result);

    //     } catch (Exception ex) {
    //         return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
    //                 .body("Upstream service error.");
    //     }
    // }

    // // getLatestMeal
    // @GetMapping("/getlatestmeal")
    // public ResponseEntity<?> getLatestMeal(
    //         HttpServletRequest request
    // ) {
    //     // 1. Extract token from header
    //     String authHeader = request.getHeader("Authorization");       
    //     String username = request.getHeader("X-username"); // Optional: for logging or additional checks 
    //     if (authHeader == null || !authHeader.startsWith("Bearer ")) {
    //         return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
    //                 .body("Unauthorized: missing or malformed Authorization header.");
    //     }
    //     String token = authHeader.substring(7);

    //     // 2. Validate token
    //     List<String> scopes;
    //     try {
    //         scopes = tokenService.getScopes(token);
    //     } catch (Exception e) {
    //         return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
    //                 .body("Unauthorized: " + e.getMessage());
    //     }

    //     if (scopes.contains("Invalid Token") || !scopes.contains("ReadUser")) {
    //         return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
    //                 .body("Unauthorized: invalid token.");
    //     }

    //     // 3. Call USDA API
    //     try {
    //        String sub = tokenService.getSub(token);
    //        Meal result = memberMealStore.getLatestMeal(sub != null ? sub : username);

    //         return ResponseEntity.ok(result);

    //     } catch (Exception ex) {
    //         return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
    //                 .body("Upstream service error.");
    //     }
    // }

    // // getLatestMeal
    // @GetMapping("/getlatestmealid")
    // public ResponseEntity<String> getLatestMealId(
    //         HttpServletRequest request
    // ) {
    //     // 1. Extract token from header
    //     String authHeader = request.getHeader("Authorization");       
    //     String username = request.getHeader("X-username"); // Optional: for logging or additional checks 
    //     if (authHeader == null || !authHeader.startsWith("Bearer ")) {
    //         return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
    //                 .body("Unauthorized: missing or malformed Authorization header.");
    //     }
    //     String token = authHeader.substring(7);

    //     // 2. Validate token
    //     List<String> scopes;
    //     try {
    //         scopes = tokenService.getScopes(token);
    //     } catch (Exception e) {
    //         return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
    //                 .body("Unauthorized: " + e.getMessage());
    //     }

    //     if (scopes.contains("Invalid Token") || !scopes.contains("ReadUser")) {
    //         return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
    //                 .body("Unauthorized: invalid token.");
    //     }

    //     // 3. Call USDA API
    //     try {
    //        String sub = tokenService.getSub(token);
    //        String result = memberMealStore.getLatestMealId(sub != null ? sub : username);

    //         return ResponseEntity.ok(result);

    //     } catch (Exception ex) {
    //         return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
    //                 .body("Upstream service error.");
    //     }
    // }

    // @GetMapping("/getmealtypes")
    // public ResponseEntity<?> getMealTypes(    
    //         @RequestParam String mealType,        
    //         HttpServletRequest request
    // ) {
    //     // 1. Extract token from header
    //     String authHeader = request.getHeader("Authorization");       
    //     String username = request.getHeader("X-username"); // Optional: for logging or additional checks 
    //     if (authHeader == null || !authHeader.startsWith("Bearer ")) {
    //         return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
    //                 .body("Unauthorized: missing or malformed Authorization header.");
    //     }
    //     String token = authHeader.substring(7);

    //     // 2. Validate token
    //     List<String> scopes;
    //     String sub;
    //     try {
    //         sub = tokenService.getSub(token);
    //         scopes = tokenService.getScopes(token);
    //     } catch (Exception e) {
    //         return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
    //                 .body("Unauthorized: " + e.getMessage());
    //     }

    //     if (scopes.contains("Invalid Token") || !scopes.contains("ReadUser")) {
    //         return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
    //                 .body("Unauthorized: invalid token.");
    //     }

    //     // 3. Call USDA API
    //     try {
    //        List<Meal> result = memberMealStore.getMealTypes(sub != null ? sub : username, mealType);

    //         return ResponseEntity.ok(result);

    //     } catch (Exception ex) {
    //         return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
    //                 .body("Upstream service error.");
    //     }
    // }
    
    // // Controller method
    // @PostMapping("/addmeal")
    // public ResponseEntity<String> addFood(@Valid @RequestBody AddFoodRequest request,
    //     HttpServletRequest headerRequest
    // ) {
    //     try {
    //          // 1. Extract token from header
    //         String authHeader = headerRequest.getHeader("Authorization");
    //         String username = headerRequest.getHeader("X-username"); // Optional: for logging or additional checks
    //         if (authHeader == null || !authHeader.startsWith("Bearer ")) {
    //             return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
    //                     .body("Unauthorized: missing or malformed Authorization header.");
    //         }
    //         String token = authHeader.substring(7);

    //         // 2. Validate token
    //         List<String> scopes;

    //         String sub = null;
    //         try {
    //             sub = tokenService.getSub(token);
    //             scopes = tokenService.getScopes(token);
    //         } catch (Exception e) {
    //             return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
    //                     .body("Unauthorized: " + e.getMessage());
    //         }

    //         if (scopes.contains("Invalid Token") || !scopes.contains("WriteUser")) {
    //             return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
    //                     .body("Unauthorized: invalid token.");
    //         }
    //         String[] fdcIds = request.getFdcIds();
    //         String type = request.getType();
    
    //         List<Food> existingFoods = foodParser
    //                                         .getFoodsByFdcIdsFoods(fdcIds)
    //                                         .stream()
    //                                         .filter(f -> f.getCalories() > 0)
    //                                         .toList(); 
    //         if (existingFoods.size() > 0) {
    //             // Some foods found in local list
    //             Meal meal = new Meal(type, existingFoods);
    //             memberMealStore.addMeal(sub != null ? sub : username, meal); // Example member ID

    //             return ResponseEntity.ok(meal.getId());
    //         }
            
    //         return ResponseEntity.ok("");
    //     } catch (Exception e) {
    //         e.printStackTrace();
    //         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("");
    //     }
    // }

    // // Controller method
    // @PutMapping("/removemealfooditem")
    // public ResponseEntity<String> removemealfooditem(@Valid @RequestBody RemoveFoodRequest request,
    //     HttpServletRequest headerRequest
    // ) {
    //     // return ResponseEntity.ok("removemealfooditem");
    //     try {
    //          // 1. Extract token from header
    //         String authHeader = headerRequest.getHeader("Authorization");
    //         String username = headerRequest.getHeader("X-username"); // Optional: for logging or additional checks
    //         if (authHeader == null || !authHeader.startsWith("Bearer ")) {
    //             return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
    //                     .body("Unauthorized: missing or malformed Authorization header.");
    //         }
    //         String token = authHeader.substring(7);

    //         // 2. Validate token
    //         List<String> scopes;

    //         String sub = null;
    //         try {
    //             sub = tokenService.getSub(token);
    //             scopes = tokenService.getScopes(token);
    //         } catch (Exception e) {
    //             return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
    //                     .body("Unauthorized: " + e.getMessage());
    //         }

    //         if (scopes.contains("Invalid Token") || !scopes.contains("WriteUser")) {
    //             return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
    //                     .body("Unauthorized: invalid token.");
    //         }
    //         String[] fdcIds = request.getFdcIds();
    //         String mealId = request.getMealId();
    
    //         memberMealStore.removemealfooditem(sub != null ? sub : username, mealId,  Arrays.asList(fdcIds));
            
    //         return ResponseEntity.ok("No content");
    //     } catch (Exception e) {
    //         e.printStackTrace();
    //         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred while processing the request.");
    //     }
    // }

    // // Controller method
    // @DeleteMapping("/remove")
    // public ResponseEntity<?> removeMeal(String mealId,
    //     HttpServletRequest headerRequest
    // ) {
    //     // return ResponseEntity.ok("removemealfooditem");
    //     try {
    //          // 1. Extract token from header
    //         String authHeader = headerRequest.getHeader("Authorization");
    //         String username = headerRequest.getHeader("X-username"); // Optional: for logging or additional checks
    //         if (authHeader == null || !authHeader.startsWith("Bearer ")) {
    //             return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
    //                     .body("Unauthorized: missing or malformed Authorization header.");
    //         }
    //         String token = authHeader.substring(7);

    //         // 2. Validate token
    //         List<String> scopes;

    //         String sub = null;
    //         try {
    //             sub = tokenService.getSub(token);
    //             scopes = tokenService.getScopes(token);
    //         } catch (Exception e) {
    //             return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
    //                     .body("Unauthorized: " + e.getMessage());
    //         }

    //         if (scopes.contains("Invalid Token") || !scopes.contains("WriteUser")) {
    //             return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
    //                     .body("Unauthorized: invalid token.");
    //         }
            
    //         memberMealStore.removeMeal(sub != null ? sub : username, mealId);
            
    //         return ResponseEntity.status(HttpStatus.OK)
    //                 .body("MealId: " + mealId + " removed.");
    //     } catch (Exception e) {
    //         e.printStackTrace();
    //         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred while processing the request.");
    //     }
    // }

    // // Controller method
    // @PostMapping("/addimage")
    // public ResponseEntity<String> loadImage(HttpServletRequest headerRequest
    // ) {
    //     try {
    //          // 1. Extract token from header
    //         String authHeader = headerRequest.getHeader("Authorization");
    //         String username = headerRequest.getHeader("X-username"); // Optional: for logging or additional checks
    //         if (authHeader == null || !authHeader.startsWith("Bearer ")) {
    //             return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
    //                     .body("Unauthorized: missing or malformed Authorization header.");
    //         }
    //         String token = authHeader.substring(7);

    //         // 2. Validate token
    //         List<String> scopes;
    //         try {
    //             scopes = tokenService.getScopes(token);
    //         } catch (Exception e) {
    //             return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
    //                     .body("Unauthorized: " + e.getMessage());
    //         }

    //         if (scopes.contains("Invalid Token") || !scopes.contains("WriteUser")) {
    //             return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
    //                     .body("Unauthorized: invalid token.");
    //         }

    //         String url = ninjaApiUrl + "/v1/imagetext";
    //         HttpRequest request = HttpRequest.newBuilder()
    //                 .uri(URI.create(url))
    //                 .GET()
    //                 .build();

    //             return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
    //                 .thenAccept(response -> {
            
            
    //         return ResponseEntity.ok("Some foods not found.");
    //     } catch (Exception e) {
    //         e.printStackTrace();
    //         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred while processing the request.");
    //     }
    // }
}