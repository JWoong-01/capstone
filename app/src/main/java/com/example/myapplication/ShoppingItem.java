package com.example.myapplication;

public class ShoppingItem {
    private int id;
    private String name;
    private int quantity;
    private String unit;
    private int image;  // 이미지 리소스 ID 추가
    private boolean isChecked = false; // 선택 상태

    // 생성자 (DB에서 로드된 항목)
    public ShoppingItem(int id, String name, int quantity, String unit) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.unit = unit;
        this.image = IngredientData.getImageResource(IngredientData.getMatchedKoreanName(name));

    }

    // 생성자 (새 항목)
    public ShoppingItem(String name, int quantity, String unit) {
        this(-1, name, quantity, unit);  // id는 임시로 -1 사용
    }

    // Getter
    public int getId() { return id; }
    public int getImage() {return  image;}
    public String getName() { return name; }
    public int getQuantity() { return quantity; }
    public String getUnit() { return unit; }
    public boolean isChecked() { return isChecked; } // 선택 상태 확인

    // Setter
    public  void setImage(int image) {this.image = image;}
    public void setId(int id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public void setUnit(String unit) { this.unit = unit; }
    public void setChecked(boolean checked) { this.isChecked = checked; } // 선택 상태 변경
}
