package com.example.myapplication;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.common.InputImage;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;
import androidx.camera.view.PreviewView;
import android.content.pm.PackageManager;
import java.util.HashSet;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BarcodeScannerActivity extends AppCompatActivity {
    private PreviewView cameraPreview;
    private ExecutorService cameraExecutor;
    private BarcodeScanner scanner;
    private HashSet<String> scannedBarcodes = new HashSet<>();
    private ProcessCameraProvider cameraProvider;
    private ImageAnalysis imageAnalysis;
    private boolean isScanning = false;  // 🔥 버튼 눌렀을 때만 스캔하도록 설정

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barcode_scanner);

        cameraPreview = findViewById(R.id.preview_view);
        ImageButton scanButton = findViewById(R.id.button_capture);  // 🔥 버튼 추가 (레이아웃에서 추가 필요)

        scanner = BarcodeScanning.getClient();
        cameraExecutor = Executors.newSingleThreadExecutor();

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            setupCamera();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CAMERA}, 1);
        }

        // 🔥 버튼 클릭 시 스캔 실행
        scanButton.setOnClickListener(v -> startScanning());
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
                    if (!isScanning) {
                        imageProxy.close();
                        return;
                    }

                    try {
                        InputImage image = InputImage.fromMediaImage(imageProxy.getImage(), imageProxy.getImageInfo().getRotationDegrees());

                        scanner.process(image)
                                .addOnSuccessListener(barcodes -> {
                                    for (Barcode barcode : barcodes) {
                                        String barcodeValue = barcode.getRawValue();

                                        if (barcodeValue != null && !scannedBarcodes.contains(barcodeValue)) {
                                            scannedBarcodes.add(barcodeValue);
                                            Log.d("Barcode", "Scanned: " + barcodeValue);

                                            runOnUiThread(() -> Toast.makeText(this, "상품 정보 검색: " + barcodeValue, Toast.LENGTH_SHORT).show());

                                            fetchProductInfo(barcodeValue);
                                            isScanning = false; // 🔥 스캔 후 다시 버튼을 눌러야 가능하도록 설정
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

            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }, ContextCompat.getMainExecutor(this));
    }

    // 🔥 버튼 클릭 시 스캔 활성화
    private void startScanning() {
        scannedBarcodes.clear();
        isScanning = true;
        Toast.makeText(this, "스캔을 시작합니다. 바코드를 카메라에 맞춰주세요.", Toast.LENGTH_SHORT).show();
    }

    private void fetchProductInfo(String barcode) {
        Log.d("API", "Fetching product info for barcode: " + barcode);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cameraExecutor.shutdown();
    }
}
