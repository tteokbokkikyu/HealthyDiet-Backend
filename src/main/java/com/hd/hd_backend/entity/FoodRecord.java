package com.hd.hd_backend.entity;

import com.fasterxml.jackson.databind.ObjectMapper;

public class FoodRecord {

    private Integer foodRecordId;

    private String recordTime;

    private Integer userId;

    private Integer foodId;

    private Double foodWeight;

    private Integer calories;

    private Double fat;

    private Double protein;

    private Double carbohydrates;

    private Double sodium;

    private Double potassium;

    private Double dietaryFiber;

    // Getters and Setters
    public Integer getFoodRecordId() {
        return foodRecordId;
    }

    public void setFoodRecordId(Integer foodRecordId) {
        this.foodRecordId = foodRecordId;
    }

    public String getRecordTime() {
        return recordTime;
    }

    public void setRecordTime(String recordTime) {
        this.recordTime = recordTime;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getFoodId() {
        return foodId;
    }

    public void setFoodId(int foodId) {
        this.foodId = foodId;
    }

    public Double getFoodWeight() {
        return foodWeight;
    }

    public void setFoodWeight(Double foodWeight) {
        this.foodWeight = foodWeight;
    }

    public Integer getCalories() {
        return calories;
    }

    public void setCalories(Integer calories) {
        this.calories = calories;
    }

    public Double getFat() {
        return fat;
    }

    public void setFat(Double fat) {
        this.fat = fat;
    }

    public Double getProtein() {
        return protein;
    }

    public void setProtein(Double protein) {
        this.protein = protein;
    }

    public Double getCarbohydrates() {
        return carbohydrates;
    }

    public void setCarbohydrates(Double carbohydrates) {
        this.carbohydrates = carbohydrates;
    }

    public Double getSodium() {
        return sodium;
    }

    public void setSodium(Double sodium) {
        this.sodium = sodium;
    }

    public Double getPotassium() {
        return potassium;
    }

    public void setPotassium(Double potassium) {
        this.potassium = potassium;
    }

    public Double getDietaryFiber() {
        return dietaryFiber;
    }

    public void setDietaryFiber(Double dietaryFiber) {
        this.dietaryFiber = dietaryFiber;
    }

    public String foodRecordDetails() {
        // 使用 ObjectMapper 进行对象转换
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            // 将 FoodRecord 对象转换为 JSON 字符串
            return objectMapper.writeValueAsString(this);
        } catch (Exception e) {
            // 错误处理
            return "{\"error_code\":500,\"error_message\":\"转换为JSON失败\"}";
        }
    }
}
