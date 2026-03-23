package com.example.demo.DataSource;

import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import com.example.demo.BusinessObjects.Meal;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.ArrayListMultimap;
 
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
 
    // Remove a specific meal from a member
    public void removeMeal(String memberId, Meal meal) {
        List<Meal> meals = memberMeals.get(memberId);
        if (meals != null) {
            meals.remove(meal);
        }
    }
 
    // Remove all meals for a member
    public void removeMember(String memberId) {
        memberMeals.remove(memberId);
    }
 
    // Check if a member exists
    public boolean memberExists(String memberId) {
        return memberMeals.containsKey(memberId);
    }
 
    // Get total number of members
    public int count() {
        return memberMeals.size();
    }
 
    // Get all member meal relations
    public Map<String, List<Meal>> getAll() {
        return memberMeals;
    }
}
 