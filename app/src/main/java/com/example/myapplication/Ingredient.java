package com.example.myapplication;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class Ingredient implements Serializable {
    private String name;
    private int quantity;
    private String unit;
    private final String intakeDate;
    private Calendar expirationDate;
    private String storageLocation;
    private int imageResId;

    public Ingredient(String name, int quantity, String unit, String intakeDate, Calendar expirationDate, String storageLocation, int imageResId) {
        this.name = name;
        this.quantity = quantity;
        this.unit = unit;
        this.intakeDate = intakeDate;
        this.expirationDate = expirationDate;
        this.storageLocation = storageLocation;
        this.imageResId = imageResId;
    }

    // Getter
    public String getName() {
        return name;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getUnit() {
        return unit;
    }

    public Calendar getExpirationDate() {
        return expirationDate;
    }

    public String getFormattedExpirationDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return dateFormat.format(expirationDate.getTime());
    }

    public String getStorageLocation() {
        return storageLocation;
    }

    public int getImageResId() {
        return imageResId;
    }

    public String getIntakeDate() {
        return intakeDate;
    }

    // Setter (추가)
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public void setExpirationDate(Calendar expirationDate) {
        this.expirationDate = expirationDate;
    }

    public void setStorageLocation(String storageLocation) {
        this.storageLocation = storageLocation;
    }

    // 유통기한 체크
    public boolean isExpiringSoon() {
        Calendar today = Calendar.getInstance();
        long diffInMillis = expirationDate.getTimeInMillis() - today.getTimeInMillis();
        long diffInDays = diffInMillis / (1000 * 60 * 60 * 24);
        return diffInDays >= 0 && diffInDays <= 3;
    }

    public boolean isExpired() {
        Calendar today = Calendar.getInstance();
        return expirationDate.before(today);
    }

    public String calculateDDay() {
        Calendar today = Calendar.getInstance();
        long diffInMillis = expirationDate.getTimeInMillis() - today.getTimeInMillis();
        long diffInDays = TimeUnit.MILLISECONDS.toDays(diffInMillis);

        if (diffInDays == 0) {
            return "D-Day";
        } else if (diffInDays > 0) {
            return "D-" + diffInDays;
        } else {
            return "D+" + Math.abs(diffInDays);
        }
    }
}
