package com.example.demo.BusinessObjects;

public class Food {
    private String category;
    private String name;
    private String fdcId;
    private int calories;

    public Food(String category, String name, String fdcId, int calories) {
        this.category = category;
        this.name = name    ;
        this.fdcId = fdcId;
        this.calories = calories;
    }

    public int getCalories() {
        return calories;
    }
    
    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFdcId() {
        return fdcId;
    }

    public void setFdcId(String fdcId) {
        this.fdcId = fdcId;
    }

    public void setCalories(int calories) {
        this.calories = calories;
    }
    
}
