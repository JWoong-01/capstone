package com.example.myapplication;

public class Ingredient {
    private String name;
    private int quantity;
    private String expirationDate;

    // 생성자
    public Ingredient(String name, int quantity, String expirationDate) {
        this.name = name;
        this.quantity = quantity;
        this.expirationDate = expirationDate;
    }

    // Getter 메서드
    public String getName() {
        return name;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getExpirationDate() {
        return expirationDate;
    }
}
