package com.example.myapplication;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;
import android.content.pm.PackageManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.barcode.common.Barcode;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ScanReceipt extends AppCompatActivity {

    private PreviewView cameraPreview;
    private ExecutorService cameraExecutor;
    private BarcodeScanner scanner;
    private ProcessCameraProvider cameraProvider;
    private ImageAnalysis imageAnalysis;
    private boolean isScanning = false; // 🔥 버튼을 눌렀을 때만 스캔

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scan_receipt);

        cameraPreview = findViewById(R.id.camera_preview);
        ImageButton scanButton = findViewById(R.id.button_capture); // 🔥 스캔 버튼 추가

        scanner = BarcodeScanning.getClient();
        cameraExecutor = Executors.newSingleThreadExecutor();

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            setupCamera();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CAMERA}, 1);
        }

        // 🔥 버튼 클릭 시 스캔 활성화
        scanButton.setOnClickListener(v -> startScanning());

        // 취소 버튼
        Button cancelButton = findViewById(R.id.btn_cancel);
        cancelButton.setOnClickListener(v -> {
            Intent intent = new Intent(ScanReceipt.this, HomeActivity.class);
            startActivity(intent);
            finish();
        });

        // 영수증 버튼
        Button scanText = findViewById(R.id.scan_text);
        scanText.setOnClickListener(v -> {
            Intent intent = new Intent(ScanReceipt.this, ReceiptScannerActivity.class);
            startActivity(intent);
        });

        // 직접입력 버튼
        Button inputButton = findViewById(R.id.tab_input);
        inputButton.setOnClickListener(v -> {
            Intent intent = new Intent(ScanReceipt.this, AddIngredientActivity.class);
            startActivity(intent);
        });
    }

    private void setupCamera() {
        ProcessCameraProvider.getInstance(this).addListener(() -> {
            try {
                cameraProvider = ProcessCameraProvider.getInstance(this).get();
                Preview preview = new Preview.Builder().build();
                preview.setSurfaceProvider(cameraPreview.getSurfaceProvider());

                CameraSelector cameraSelector = new CameraSelector.Builder()
                        .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                        .build();

                imageAnalysis = new ImageAnalysis.Builder().build();
                imageAnalysis.setAnalyzer(cameraExecutor, imageProxy -> {
                    if (!isScanning) { // 🔥 버튼을 눌렀을 때만 실행
                        imageProxy.close();
                        return;
                    }

                    try {
                        InputImage image = InputImage.fromMediaImage(imageProxy.getImage(), imageProxy.getImageInfo().getRotationDegrees());

                        scanner.process(image)
                                .addOnSuccessListener(barcodes -> {
                                    for (Barcode barcode : barcodes) {
                                        String barcodeValue = barcode.getRawValue();
                                        if (barcodeValue != null) {
                                            isScanning = false; // 🔥 한 번 스캔 후 비활성화
                                            fetchProductInfo(barcodeValue);
                                        }
                                    }
                                })
                                .addOnFailureListener(e -> Log.e("Barcode", "Failed to scan barcode", e))
                                .addOnCompleteListener(task -> imageProxy.close());
                    } catch (Exception e) {
                        e.printStackTrace();
                        imageProxy.close();
                    }
                });

                cameraProvider.bindToLifecycle((LifecycleOwner) this, cameraSelector, preview, imageAnalysis);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }, ContextCompat.getMainExecutor(this));
    }

    // 🔥 버튼 클릭 시 스캔 활성화
    private void startScanning() {
        isScanning = true;
        Toast.makeText(this, "스캔을 시작합니다. 바코드를 카메라에 맞춰주세요.", Toast.LENGTH_SHORT).show();
    }


    private void fetchProductInfo(String barcode) {
        Toast.makeText(this, "상품 정보 검색: " + barcode, Toast.LENGTH_SHORT).show();

        isScanning = false;  // 한 번 스캔하면 자동 스캔 멈춤 (버튼 다시 눌러야 재시작)

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
                setupCamera();
            } else {
                Toast.makeText(this, "카메라 권한을 허용해야 합니다.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
