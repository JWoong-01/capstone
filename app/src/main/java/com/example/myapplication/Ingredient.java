package com.example.myapplication;

public class Ingredient {
    private String name;
    private int quantity;
    private final String intakeDate;
    private String expirationDate;
    private int imageResId;  // 이미지 리소스를 int 타입으로 저장

    // 생성자
    public Ingredient(String name, int quantity, String expirationDate, String intakeDate, int imageResId) {
        this.name = name;
        this.quantity = quantity;
        this.intakeDate = intakeDate;
        this.expirationDate = expirationDate;
        this.imageResId = imageResId;  // 리소스 ID로 저장
    }

    // Getter 및 Setter
    public String getName() {
        return name;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getExpirationDate() {
        return expirationDate;
    }

    public int getImageResId() {
        return imageResId;  // 리소스 ID를 반환
    }

    public String getIntakeDate() { return intakeDate;
    }
}

