package com.example.myapplication;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.Calendar;
import android.util.Log; // Log 사용 해서 확인
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call; // Retrofit Call 사용
import retrofit2.Callback; // Callback 사용
import retrofit2.Response; // Response 사용
import retrofit2.Retrofit; // Retrofit 사용
import retrofit2.converter.gson.GsonConverterFactory; // GsonConverterFactory 사용

public class SettingActivity extends AppCompatActivity {
    private SwitchCompat switchNotification;
    private SwitchCompat switchCamera;
    private static final int CAMERA_PERMISSION_CODE = 100;
    private static final int BARCODE_REQUEST_CODE = 101; // 바코드 스캔 요청 코드

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        switchNotification = findViewById(R.id.switch_notification);
        switchCamera = findViewById(R.id.switch_camera);

        // SharedPreferences 초기화
        SharedPreferences sharedPreferences = getSharedPreferences("settings", MODE_PRIVATE);
        boolean isNotificationEnabled = sharedPreferences.getBoolean("notifications", false);
        boolean isCameraEnabled = sharedPreferences.getBoolean("camera_permission", false);

        switchNotification.setChecked(isNotificationEnabled);
        switchCamera.setChecked(isCameraEnabled);

        // 알림 스위치 리스너
        switchNotification.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("notifications", isChecked);
            editor.apply();

            if (isChecked) {
                scheduleDailyNotification();
            } else {
                cancelScheduledNotification();
            }
        });

        // 카메라 권한 요청 스위치 리스너
        switchCamera.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                requestCameraPermission();
            } else {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("camera_permission", false);
                editor.apply();
            }
        });


    }

    // 바코드 스캔 결과 처리
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == BARCODE_REQUEST_CODE && resultCode == RESULT_OK) {
            if (data != null) {
                String barcode = data.getStringExtra("barcode");
                if (barcode != null) {
                    fetchProductInfo(barcode);
                }
            }
        }
    }

    // 상품 정보 조회
    private void fetchProductInfo(String barcode) {
        // Retrofit 로깅 설정
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://world.openfoodfacts.org/")
                .client(client)  // OkHttpClient 설정
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        BarcodeApiService apiService = retrofit.create(BarcodeApiService.class);
        Call<ProductResponse> call = apiService.getProduct(barcode);
//추후 ScanReceipt 파일로 이동 예정
        call.enqueue(new Callback<ProductResponse>() {
            @Override
            public void onResponse(Call<ProductResponse> call, Response<ProductResponse> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        ProductResponse.Product product = response.body().product;
                        Log.d("Barcode", "Product Name: " + product.product_name);
                        Log.d("Barcode", "Brand: " + product.brands);
                        Log.d("Barcode", "Image URL: " + product.image_url);
                        Toast.makeText(SettingActivity.this, "상품: " + product.product_name, Toast.LENGTH_SHORT).show();
                    } else {
                        Log.e("Barcode", "No product found for barcode: " + barcode);
                        Toast.makeText(SettingActivity.this, "해당 바코드의 제품을 찾을 수 없습니다.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.e("Barcode", "API 응답 실패: " + response.code());
                    Toast.makeText(SettingActivity.this, "상품 정보를 불러오는 데 실패했습니다. 응답 코드: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ProductResponse> call, Throwable t) {
                Log.e("Barcode", "API 호출 실패", t);
                Toast.makeText(SettingActivity.this, "상품 정보를 불러오는 데 실패했습니다. 에러: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // 카메라 권한 요청
    private void requestCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
        } else {
            Toast.makeText(this, "카메라 권한이 이미 허용됨", Toast.LENGTH_SHORT).show();
        }
    }

    // 권한 요청 결과 처리
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                SharedPreferences sharedPreferences = getSharedPreferences("settings", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("camera_permission", true);
                editor.apply();
                Toast.makeText(this, "카메라 권한이 허용되었습니다.", Toast.LENGTH_SHORT).show();
            } else {
                switchCamera.setChecked(false);
                Toast.makeText(this, "카메라 권한이 거부되었습니다.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // 알림 설정
    private void scheduleDailyNotification() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 9);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        Intent notificationIntent = new Intent(this, NotificationReceiver.class);
        notificationIntent.putExtra("checkExpiration", true);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        if (alarmManager != null) {
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
        }
    }

    // 알림 해제
    private void cancelScheduledNotification() {
        Intent notificationIntent = new Intent(this, NotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        if (alarmManager != null) {
            alarmManager.cancel(pendingIntent);
        }
    }
}