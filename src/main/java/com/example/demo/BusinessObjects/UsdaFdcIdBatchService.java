package com.example.demo.BusinessObjects;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class UsdaFdcIdBatchService {

    @Value("${USDAapiKey}")
    private String usdaApiKey;

    @Value("${USDAapiUrl}")
    private String usdaApiUrl;

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();    

    public UsdaFdcIdBatchService() {
    }

    public CompletableFuture<Void> processfdcIdsBatchAsync(List<String> fdcIdsList, List<Food> foodList) {
                    
        // Create async tasks for each fdcId — equivalent to Task.WhenAll
        List<CompletableFuture<Void>> tasks = fdcIdsList.stream()
            .map(fdcId -> {
                String url = usdaApiUrl + "/fdc/v1/food/" + fdcId + "?format=abridged&api_key=" + usdaApiKey;

                HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();

                return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenAccept(response -> {
                        try {
                            JsonNode root = objectMapper.readTree(response.body());
                            
                            // Log description for debugging
                            System.out.println("Processing: " + root.path("description").asText());
                            
                            // Find matching Food object from foodList by fdcId
                            Food matchingFood = foodList.stream()
                                .filter(food -> food.getFdcId().equals(fdcId))
                                .findFirst()
                                .orElse(null);
                            
                            System.out.println("Updated: " + matchingFood.getName() + " → " + matchingFood.getCalories() + " kcal (before)"); // Log before update  
                            // Extract calories from foodNutrients array
                            JsonNode nutrientsNode = root.path("foodNutrients");
                            if (nutrientsNode.isArray()) {
                                for (JsonNode nutrient : nutrientsNode) {
                                    String nutrientName = nutrient.path("name").asText("");
                                    String unitName = nutrient.path("unitName").asText("");
                                    if (nutrientName.equalsIgnoreCase("Energy") && unitName.equalsIgnoreCase("KCAL")) {
                                        int calories = nutrient.path("amount").asInt(0);
                                        matchingFood.setCalories(calories);
                                        System.out.println("Updated: " + matchingFood.getName() + " → " + calories + " kcal");
                                        break;
                                    }
                                }
                            }                              
                            
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        // return BigDecimal.ZERO; // Return dummy value since we don't need it                        
                    });
            })
            .toList();         
         // Wait for all tasks to complete then return Void
        return CompletableFuture.allOf(tasks.toArray(new CompletableFuture[0]));
    }
}