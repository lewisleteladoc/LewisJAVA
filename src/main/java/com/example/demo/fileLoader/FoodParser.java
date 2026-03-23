package com.example.demo.fileLoader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.core.io.ClassPathResource;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import com.example.demo.BusinessObjects.Food;
import com.example.demo.BusinessObjects.UsdaFdcIdBatchService;

@Service
public class FoodParser {

    private List<Food> foodList = new ArrayList<Food>();
    private final UsdaFdcIdBatchService usdaFdcIdBatchService;

    public FoodParser(UsdaFdcIdBatchService usdaFdcIdBatchService) {
        this.usdaFdcIdBatchService = usdaFdcIdBatchService;
    }
    
    public List<Food> getFoodList() {
        return foodList;
    }

    public List<Food> getFoodsByFdcIdsFoods(String[] fdcIds) {
        return foodList.stream()
                .filter(food -> {
                    for (String fdcId : fdcIds) {
                        if (food.getFdcId().equals(fdcId)) {
                            return true;
                        }
                    }
                    return false;
                })
                .toList();
    }

    public Food getFoodByFdcId(String fdcId) {
        return foodList.stream()
                .filter(food -> food.getFdcId().equals(fdcId))
                .findFirst()
                .orElse(null);
    }   

    public void parseFoodFile() {       
        System.out.println("================================");
        System.out.println("FoodParser.parseFoodFile()...");
        System.out.println("================================");


        try {
            List<String> fdcIdsList = new ArrayList<>();
            ClassPathResource resource = new ClassPathResource("mockdata/food.txt");
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(resource.getInputStream())
                );

            int calories = 0; // Placeholder, replace with actual parsing if available
            reader.lines()
                .skip(1) // Skip header
                .map(line -> line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)"))
                .forEach(parts -> {
                    String category = parts[0];
                    String name = parts[1].replace("\"", "");
                    String fdcId = parts[2];                                           
                    
                    foodList.add(new Food(category, name, fdcId, calories));     
                    fdcIdsList.add(fdcId);           
                });

            reader.close();
            usdaFdcIdBatchService.processfdcIdsBatchAsync(fdcIdsList, foodList);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
