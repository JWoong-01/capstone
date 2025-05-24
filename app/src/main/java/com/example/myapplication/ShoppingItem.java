package com.example.myapplication;

public class ShoppingItem {
    private int id;             // ← 새로 추가된 필드
    private String name;
    private int quantity;
    private String unit;

    // 생성자 (DB에 저장된 항목)
    public ShoppingItem(int id, String name, int quantity, String unit) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.unit = unit;
    }

    // 생성자 (아직 DB에 저장되지 않은 새 항목)
    public ShoppingItem(String name, int quantity, String unit) {
        this(-1, name, quantity, unit);  // id는 임시로 -1 사용
    }

    // Getter
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getUnit() {
        return unit;
    }

    // Setter
    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }
}
