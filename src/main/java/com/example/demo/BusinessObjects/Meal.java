package com.example.demo.BusinessObjects;

import java.util.List;
import java.util.UUID;
import java.util.Date;

public class Meal {
    private String id;
    private int totalCalories;
    private String type; // breakfast, lunch, dinner, snack
    private List<Food> foods;
    private Date date;

    public Meal(String type, List<Food> foods) {        
        this.type = type;
        this.foods = foods;        
        this.totalCalories = foods.size() > 0 ? foods.stream()
                .mapToInt(food -> food.getCalories())
                .sum() : 0;
        this.date = new Date();                
        id = UUID.randomUUID().toString();
    }

    public String getId() {
        return id;
    }
    
    public int getTotalCalories() {
        return totalCalories;
    }

    public String getType() {
        return type;
    }   

    public List<Food> getFoods() {
        return foods;
    }

    public Date getDate() {
        return date;        
    }

    public void removeFoodItem(String fdcId) {
        try {
            // Filter out the item and reassign to the list variable
            this.foods = foods.stream()
                .filter(f -> !f.getFdcId().equalsIgnoreCase(fdcId))
                .toList(); // Use .collect(Collectors.toList()) if on Java 15 or below
        } catch (Exception ex) {
            System.out.println(ex.toString());
        }        
    }

    public void removeFoodItems(List<String> fdcIds) {        
        fdcIds.forEach(fdcId -> removeFoodItem(fdcId));
    }
}
