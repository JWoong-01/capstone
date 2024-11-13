package com.example.myapplication;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class Ingredient implements Serializable {
    private String name;
    private int quantity;
    private final String intakeDate;
    private Calendar  expirationDate;
    private String storageLocation;
    private int imageResId;  // 이미지 리소스를 int 타입으로 저장


    // 생성자
    public Ingredient(String name, int quantity,  String intakeDate, Calendar expirationDate, String storageLocation, int imageResId) {
        this.name = name;
        this.quantity = quantity;
        this.intakeDate = intakeDate;
        this.expirationDate = expirationDate;
        this.storageLocation = storageLocation;
        this.imageResId = imageResId;  // 리소스 ID로 저장
    }

    // Getter 및 Setter
    public String getName() {
        return name;
    }

    public int getQuantity() {
        return quantity;
    }

    public Calendar getExpirationDate() {return expirationDate;}
    public String getFormattedExpirationDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return dateFormat.format(expirationDate.getTime());
    }

    public String getStorageLocation() {return storageLocation;}

    public int getImageResId() {
        return imageResId;  // 리소스 ID를 반환
    }

    public String getIntakeDate() { return intakeDate;
    }
    // 유통기한 확인 메서드
    public boolean isExpiringSoon() {
        Calendar today = Calendar.getInstance();
        long diffInMillis = expirationDate.getTimeInMillis() - today.getTimeInMillis();
        long diffInDays = diffInMillis / (1000 * 60 * 60 * 24);  // 일 수로 차이 계산
        return diffInDays >= 0 && diffInDays <= 3;  // 유통기한이 3일 이내인 경우 true
    }

    // 유통기한 비교 메서드
    public boolean isExpired() {
        Calendar today = Calendar.getInstance();
        return expirationDate.before(today);  // 유통기한이 오늘 날짜보다 이전이면 true
    }


    public String calculateDDay() {
        Calendar today = Calendar.getInstance();

        // 현재 날짜와 유통기한 날짜 간의 차이 계산
        long diffInMillis = expirationDate.getTimeInMillis() - today.getTimeInMillis();
        long diffInDays = TimeUnit.MILLISECONDS.toDays(diffInMillis);

        if (diffInDays == 0) {
            return "D-Day";
        } else if (diffInDays > 0) {
            return "D-" + diffInDays;  // 남은 날짜
        } else {
            return "D+" + Math.abs(diffInDays);  // 지난 날짜
        }
    }
}

