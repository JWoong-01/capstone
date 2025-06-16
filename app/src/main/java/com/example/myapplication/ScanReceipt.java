package com.example.myapplication;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.common.InputImage;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.HashSet;
import java.util.Set;

import java.util.Calendar;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.Locale;
import okhttp3.RequestBody;
import okhttp3.FormBody;

import java.util.Calendar;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.Locale;

import okhttp3.FormBody;
import okhttp3.RequestBody;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Call;
import okhttp3.Callback;

import org.json.JSONObject;
import org.json.JSONException;


import java.io.IOException;

public class ScanReceipt extends AppCompatActivity {
    private PreviewView cameraPreview;
    private ExecutorService cameraExecutor;
    private BarcodeScanner scanner;
    private Set<String> scannedBarcodes = new HashSet<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scan_receipt);

        cameraPreview = findViewById(R.id.camera_preview);
        scanner = BarcodeScanning.getClient();
        cameraExecutor = Executors.newSingleThreadExecutor();

        Button cancelButton = findViewById(R.id.btn_cancel);
        Button scantext = findViewById(R.id.scan_text);
        Button inputButton = findViewById(R.id.tab_input);

        cancelButton.setOnClickListener(v -> startActivity(new Intent(this, HomeActivity.class)));
        scantext.setOnClickListener(v -> startActivity(new Intent(this, ReceiptScannerActivity.class)));
        inputButton.setOnClickListener(v -> startActivity(new Intent(this, AddIngredientActivity.class)));

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            startCamera();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.CAMERA}, 1);
        }
    }

    private void startCamera() {
        ProcessCameraProvider.getInstance(this).addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = ProcessCameraProvider.getInstance(this).get();
                Preview preview = new Preview.Builder().build();
                preview.setSurfaceProvider(cameraPreview.getSurfaceProvider());

                ImageAnalysis imageAnalysis = new ImageAnalysis.Builder().build();
                imageAnalysis.setAnalyzer(cameraExecutor, imageProxy -> {
                    try {
                        if (imageProxy.getImage() == null) {
                            runOnUiThread(() -> Toast.makeText(this, "바코드를 재스캔하세요", Toast.LENGTH_SHORT).show());
                            imageProxy.close();
                            return;
                        }
                        InputImage image = InputImage.fromMediaImage(imageProxy.getImage(), imageProxy.getImageInfo().getRotationDegrees());
                        scanner.process(image)
                                .addOnSuccessListener(barcodes -> {
                                    for (Barcode barcode : barcodes) {
                                        String barcodeValue = barcode.getRawValue();
                                        if (barcodeValue == null || barcodeValue.isEmpty()) {
                                            runOnUiThread(() -> Toast.makeText(this, "바코드를 재스캔하세요", Toast.LENGTH_SHORT).show());
                                            return;
                                        }
                                        if (scannedBarcodes.contains(barcodeValue)) {
                                            Log.d("Barcode", "이미 스캔된 바코드: " + barcodeValue);
                                            continue;
                                        }
                                        scannedBarcodes.add(barcodeValue);
                                        fetchProductInfo(barcodeValue);
                                    }
                                })
                                .addOnFailureListener(e -> Log.e("Barcode", "바코드 스캔 실패", e))
                                .addOnCompleteListener(task -> imageProxy.close());
                    } catch (Exception e) {
                        e.printStackTrace();
                        imageProxy.close();
                    }
                });

                CameraSelector cameraSelector = new CameraSelector.Builder()
                        .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                        .build();

                cameraPreview.post(() -> {
                    preview.setSurfaceProvider(cameraPreview.getSurfaceProvider());
                    cameraProvider.bindToLifecycle((LifecycleOwner) this, cameraSelector, preview, imageAnalysis);
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        }, ContextCompat.getMainExecutor(this));
    }
    private void fetchProductInfo(String barcode) {
        Log.d("Barcode", "바코드: " + barcode);

        String apiUrl = "https://world.openfoodfacts.org/api/v0/product/" + barcode + ".json";

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(apiUrl).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> Toast.makeText(ScanReceipt.this, "상품 API 호출 실패", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                try {
                    Log.d("서버응답", "result = " + result);

                    JSONObject jsonObject = new JSONObject(result);

                    if (jsonObject.has("product")) {
                        JSONObject product = jsonObject.getJSONObject("product");

                        String name = product.optString("product_name", "이름 없음");
                        runOnUiThread(() -> {
                            Toast.makeText(ScanReceipt.this, "상품 등록 성공: " + name, Toast.LENGTH_SHORT).show();
                            processProductName(name);

                        });

                    } else {
                        runOnUiThread(() -> {
                            Toast.makeText(ScanReceipt.this, "등록되지 않은 상품입니다", Toast.LENGTH_SHORT).show();
                        });
                        Log.e("JSON오류", "product 키 없음");
                    }

                } catch (JSONException e) {
                    Log.e("JSON파싱에러", "에러: " + e.getMessage());
                    runOnUiThread(() ->
                            Toast.makeText(ScanReceipt.this, "서버 응답 처리 중 오류 발생", Toast.LENGTH_SHORT).show()
                    );
                }
            }
        });
    }

    private void processProductName(String productName) {
        final String originalName = productName;
        final String keywordForImage = extractKeyword(originalName);


        final String simplifiedName = extractKeyword(productName);
        if (simplifiedName == null || simplifiedName.isEmpty()) return;

        String postUrl = "http://yju407.dothome.co.kr/add_ingredient.php";

        // POST 방식으로 데이터 전송
        OkHttpClient client = new OkHttpClient();

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, 7);
        String expirationDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.getTime());

        // 이미지 ID 자동 매핑
        int imageResId = IngredientData.getImageResource(simplifiedName);

        Log.d("ScanReceipt", " 이름: " + simplifiedName);
        Log.d("ScanReceipt", " 유통기한: " + expirationDate);
        Log.d("ScanReceipt", "️ 이미지 리소스 ID: " + imageResId);

        RequestBody requestBody = new FormBody.Builder()
                .add("name", simplifiedName)
                .add("quantity", "1") // 기본 수량
                .add("unit", "개") // 기본 단위
                .add("intakeDate", new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date()))
                .add("expiration_date", expirationDate)  // 오늘 기준 +7일로 자동 설정
                .add("storageLocation", "냉장")
                .add("image", String.valueOf(imageResId))
                .build();

        Request request = new Request.Builder()
                .url(postUrl)
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("ScanReceipt", "❌ POST 요청 실패: " + e.getMessage());
                runOnUiThread(() ->
                        Toast.makeText(ScanReceipt.this, "재료 등록 실패", Toast.LENGTH_SHORT).show()
                );
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String res = response.body().string();

                runOnUiThread(() -> {
                    Toast.makeText(ScanReceipt.this, "재료 등록 완료: " + simplifiedName, Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(ScanReceipt.this, HomeActivity.class));
                });
            }
        });
    }


    private String extractKeyword(String productName) {
        if (productName == null || productName.trim().isEmpty()) return "";
        return IngredientData.getMatchedKoreanName(productName);
    }

    private boolean containsAnyKeyword(String name, String[] keywords) {
        for (String keyword : keywords) {
            if (name.contains(keyword.toLowerCase())) {
                return true;
            }
        }
        return false;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        cameraExecutor.shutdown();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startCamera();
            } else {
                Toast.makeText(this, "카메라 권한을 허용해야 합니다.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}