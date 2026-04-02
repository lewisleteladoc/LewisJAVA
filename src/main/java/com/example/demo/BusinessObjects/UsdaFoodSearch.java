package com.example.demo.BusinessObjects;

import com.example.demo.fileLoader.FoodParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
// import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
// import java.util.concurrent.CompletableFuture;
import java.net.URLEncoder;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.List;
// import java.util.Arrays;
import java.util.ArrayList;
import com.example.demo.fileLoader.FoodParser;

@Service
public class UsdaFoodSearch {

    @Value("${USDAapiKey}")
    private String usdaApiKey;

    @Value("${USDAapiUrl}")
    private String usdaApiUrl;

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();    

    private final FoodParser foodParser;

    public UsdaFoodSearch(FoodParser foodParser) {
        this.foodParser = foodParser;
    }

    /**
     * Searches for food and returns a CompletableFuture containing the calories or an error message.
     */
    public List<Food> search(String query) throws IOException, InterruptedException {

        if (query == null || query.isBlank()) {
            throw new IllegalArgumentException("Query must not be blank");
        }

        String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);
        String url = String.format("%s/fdc/v1/foods/search?query=%s&pageSize=10",
                usdaApiUrl, encodedQuery);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Accept", "application/json")
                .header("x-api-key", usdaApiKey)
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request,
                HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new RuntimeException("USDA API error: HTTP " + response.statusCode());
        }

        List<Food> results = new ArrayList<>();

        JsonNode root = objectMapper.readTree(response.body());
        JsonNode foods = root.path("foods");

        if (foods.isArray() && !foods.isEmpty()) {
            for (JsonNode food : foods) {
                String name     = food.path("description").asText();
                String fdcId    = food.path("fdcId").asText();
                String category = food.path("foodCategory").asText("Dynamic Search");

                for (JsonNode nutrient : food.path("foodNutrients")) {
                    String nutrientName = nutrient.path("nutrientName").asText();
                    String unit         = nutrient.path("unitName").asText();

                    if (nutrientName.equalsIgnoreCase("Energy") && unit.equalsIgnoreCase("KCAL")) {
                        int calories = (int) nutrient.path("value").asDouble();
                        results.add(new Food(category, name, fdcId, calories));
                        break; // found calories for this food, move to next
                    }
                }
            }
        }

        foodParser.appendFoodList(results);
        return results;
    }
}
