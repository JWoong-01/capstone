package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;
import android.content.pm.PackageManager;  // 추가

import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;

import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.barcode.common.Barcode;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ScanReceipt extends AppCompatActivity {

    private PreviewView cameraPreview;  // 카메라 화면 미리보기
    private ExecutorService cameraExecutor;  // 카메라 쓰레드
    private BarcodeScanner scanner;  // 바코드 스캐너

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scan_receipt);

        // 카메라 권한 확인
        if (checkSelfPermission(android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            // 권한이 있을 때 초기화
            initCameraPreview();
            startCamera();
        } else {
            requestPermissions(new String[]{android.Manifest.permission.CAMERA}, 1);
        }

        // 취소 버튼 클릭
        Button cancelButton = findViewById(R.id.btn_cancel);
        cancelButton.setOnClickListener(v -> {
            Intent intent = new Intent(ScanReceipt.this, HomeActivity.class);
            startActivity(intent);
        });

        //영수증 버튼 클릭
        Button scantext = findViewById(R.id.scan_text);
        scantext.setOnClickListener(v -> {
            Intent intent = new Intent(ScanReceipt.this, ReceiptScannerActivity.class);
            startActivity(intent);
        });

        // 직접입력 버튼 클릭
        Button inputButton = findViewById(R.id.tab_input);
        inputButton.setOnClickListener(v -> {
            Intent intent = new Intent(ScanReceipt.this, AddIngredientActivity.class);
            startActivity(intent);
        });
    }

    // 카메라 미리보기 초기화 메서드
    private void initCameraPreview() {
        cameraPreview = findViewById(R.id.camera_preview);  // ID를 정확히 설정했는지 확인
        if (cameraPreview == null) {
            Log.e("ScanReceipt", "cameraPreview is null");
        } else {
            Log.d("ScanReceipt", "cameraPreview initialized");
        }
        cameraExecutor = Executors.newSingleThreadExecutor();
        scanner = BarcodeScanning.getClient();
    }

    // 카메라 시작 메서드
    private void startCamera() {
        ProcessCameraProvider.getInstance(this).addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = ProcessCameraProvider.getInstance(this).get();

                // 카메라 미리보기 설정
                Preview preview = new Preview.Builder().build();
                if (cameraPreview != null) {
                    preview.setSurfaceProvider(cameraPreview.getSurfaceProvider());
                } else {
                    Log.e("ScanReceipt", "cameraPreview is null in startCamera");
                }

                // 이미지 분석 설정
                ImageAnalysis imageAnalysis = new ImageAnalysis.Builder().build();
                imageAnalysis.setAnalyzer(cameraExecutor, imageProxy -> {
                    try {
                        // 이미지를 InputImage로 변환
                        InputImage image = InputImage.fromMediaImage(imageProxy.getImage(), imageProxy.getImageInfo().getRotationDegrees());

                        // 바코드 분석
                        scanner.process(image)
                                .addOnSuccessListener(barcodes -> {
                                    for (Barcode barcode : barcodes) {  // 바코드 처리
                                        String barcodeValue = barcode.getRawValue();
                                        Log.d("Barcode", "Scanned: " + barcodeValue);

                                        // 바코드 값으로 API 호출
                                        fetchProductInfo(barcodeValue);
                                    }
                                })
                                .addOnFailureListener(e -> Log.e("Barcode", "Failed to scan barcode", e))
                                .addOnCompleteListener(task -> imageProxy.close());
                    } catch (Exception e) {
                        e.printStackTrace();
                        imageProxy.close();
                    }
                });

                // 카메라 설정
                CameraSelector cameraSelector = new CameraSelector.Builder()
                        .requireLensFacing(CameraSelector.LENS_FACING_BACK) // 후면 카메라 사용
                        .build();

                // 카메라 바인딩
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageAnalysis);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }, getMainExecutor());
    }

    // API 호출 메서드
    private void fetchProductInfo(String barcode) {
        // API 호출 로직을 여기에 작성
        Toast.makeText(this, "Product Info for barcode: " + barcode, Toast.LENGTH_SHORT).show();

        // API 호출 후, 홈 화면으로 이동
        Intent intent = new Intent(ScanReceipt.this, HomeActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cameraExecutor.shutdown();  // 카메라 종료 시 executor도 종료
    }

    // 권한 요청 결과 처리
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 권한이 허용되었으면 초기화 후 카메라 시작
                initCameraPreview();
                startCamera();
            } else {
                Toast.makeText(this, "카메라 권한을 허용해야 합니다.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}