package com.example.demo.DataSource;

import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import com.example.demo.BusinessObjects.Meal;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.ArrayListMultimap;
import java.util.Collections;
 
@Service
public class MemberMealStore {
 
    private final ConcurrentHashMap<String, List<Meal>> memberMeals = new ConcurrentHashMap<>();    
    private final ListMultimap<String, String> memberMealRelationships = ArrayListMultimap.create();
 
    // Add a meal to a member
    public void addMeal(String memberId, Meal meal) {

        if (meal != null) {
            if (memberMeals
                .computeIfAbsent(memberId, k -> new ArrayList<>())
                .add(meal)) {
                memberMealRelationships.put(memberId, meal.getId());
            }
        }        
    }
 
    // Get all meals for a member
    public List<Meal> getMeals(String memberId) {
        return memberMeals.getOrDefault(memberId, new ArrayList<>());
    }

    // getmemberlatestnumberofmeals
    public List<Meal> getmemberlatestnumberofmeals(String memberId, int start, int count) {
        List<Meal> allMeals = memberMeals.getOrDefault(memberId, new ArrayList<>());

         // 1. Safety check for null or empty list
        if (allMeals == null || allMeals.isEmpty() || start >= allMeals.size()) {
            return new ArrayList<>();            
        }

        int total = allMeals.size();
         // 2. Calculate indices from the end (Working Backward)
        // Example: total=10, start=0, count=5 -> toIndex=10, fromIndex=5
        // Example: total=10, start=5, count=5 -> toIndex=5, fromIndex=0
        int toIndex = Math.max(0, total - start);
        int fromIndex = Math.max(0, toIndex - count);

        // 3. Extract the range
        List<Meal> range = new ArrayList<>(allMeals.subList(fromIndex, toIndex));

        // 4. Reverse the small result list so it's [Newest -> Oldest]
        Collections.reverse(range);

        return range;
    }

    public Meal getLatestMeal(String memberId) {
        List<Meal> meals = memberMeals.getOrDefault(memberId, new ArrayList<>());
        return meals.isEmpty() ? null : meals.get(meals.size() - 1);
    }

    public String getLatestMealId(String memberId) {
        Meal latestMeal = getLatestMeal(memberId);
        return latestMeal != null ? latestMeal.getId() : null;
    }

    // Get all meals for a member
    public List<Meal> getMealTypes(String memberId, String mealType) {
        List<Meal> allMeals = memberMeals.getOrDefault(memberId, new ArrayList<>());
        return allMeals.stream()
                .filter(meal -> meal.getType().equalsIgnoreCase(mealType.trim()))
                .toList();
    }
 
    // Remove a specific meal from a member
    public void removeMeal(String memberId, String mealId) {
        List<Meal> meals = memberMeals.get(memberId);
        if (meals != null) {
            if (meals.removeIf(meal -> meal.getId().equalsIgnoreCase(mealId))) {
                memberMealRelationships.remove(memberId, mealId);
            }
        }
    }
 
    // Get all member meal relations
    public Map<String, List<Meal>> getAll() {
        return memberMeals;
    }
}
 