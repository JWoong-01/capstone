package com.example.myapplication;

public class Recipe {

    private String name; // 레시피 이름
    private String imageUrl; // 이미지 URL
    private String ingredients;
    private String instructions; // 제조 과정
    public Recipe() {}

    // 생성자
    public Recipe(String name, String imageUrl, String ingredients, String instructions) {

        this.name = name;
        this.imageUrl = imageUrl;
        this.ingredients  = ingredients;
        this.instructions = instructions;
    }

    // Getters and setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    public String getIngredients() {
        return ingredients;
    }

    public void setIngredients(String ingredients) {
        this.ingredients = ingredients;
    }
}