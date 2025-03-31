package com.example.myapplication;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ReceiptScannerActivity extends AppCompatActivity {
    private PreviewView cameraPreview;
    private ExecutorService cameraExecutor;
    private TextRecognizer textRecognizer;
    private String extractedText = "";  // OCR 결과 저장 (자동 실행 방지)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receipt_scanner);

        cameraPreview = findViewById(R.id.preview_view);
        textRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);
        cameraExecutor = Executors.newSingleThreadExecutor();

        // 카메라 권한 체크
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            startCamera();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CAMERA}, 1);
        }

        // 취소 버튼 클릭
        Button cancelButton = findViewById(R.id.btn_cancel);
        cancelButton.setOnClickListener(v -> {
            Intent intent = new Intent(ReceiptScannerActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
        });

        // 바코드 버튼 클릭
        Button scanTextButton = findViewById(R.id.button_barcord);
        scanTextButton.setOnClickListener(v -> {
            Intent intent = new Intent(ReceiptScannerActivity.this, ScanReceipt.class);
            startActivity(intent);
            finish();
        });

        // 직접입력 버튼 클릭
        Button inputButton = findViewById(R.id.tab_input);
        inputButton.setOnClickListener(v -> {
            Intent intent = new Intent(ReceiptScannerActivity.this, AddIngredientActivity.class);
            startActivity(intent);
            finish();
        });

        // 🛒 "인식된 품목 확인" 버튼 클릭 시 OCR 결과 표시
        ImageButton showExtractedItemsButton = findViewById(R.id.button_capture);
        showExtractedItemsButton.setOnClickListener(v -> {
            if (!extractedText.isEmpty()) {
                extractItemsFromReceipt(extractedText);
            } else {
                Toast.makeText(this, "먼저 영수증을 스캔하세요!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // 🔥 카메라 시작 메서드
    private void startCamera() {
        ProcessCameraProvider.getInstance(this).addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = ProcessCameraProvider.getInstance(this).get();

                Preview preview = new Preview.Builder().build();
                preview.setSurfaceProvider(cameraPreview.getSurfaceProvider());

                CameraSelector cameraSelector = new CameraSelector.Builder()
                        .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                        .build();

                ImageAnalysis imageAnalysis = new ImageAnalysis.Builder().build();
                imageAnalysis.setAnalyzer(cameraExecutor, imageProxy -> {
                    try {
                        if (imageProxy.getImage() == null) {
                            imageProxy.close();
                            return;
                        }

                        InputImage image = InputImage.fromMediaImage(imageProxy.getImage(), imageProxy.getImageInfo().getRotationDegrees());

                        // 🔥 OCR 실행 (텍스트 인식)
                        textRecognizer.process(image)
                                .addOnSuccessListener(visionText -> {
                                    extractedText = visionText.getText();  // OCR 결과 저장
                                    Log.d("OCR", "Extracted Text: " + extractedText);
                                })
                                .addOnFailureListener(e -> Log.e("OCR", "Text recognition failed", e))
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

    // 🛒 텍스트에서 물품 목록 추출하는 메서드
    private void extractItemsFromReceipt(String text) {
        String[] lines = text.split("\n");
        StringBuilder items = new StringBuilder();

        for (String line : lines) {
            // 품목명과 가격이 포함된 줄을 찾는 정규식 (한글, 영어, 숫자 포함)
            if (line.matches(".*[가-힣A-Za-z]+.*") && line.matches(".*\\d{1,4}.*")) {
                items.append(line).append("\n");
            }
        }

        if (items.length() > 0) {
            Toast.makeText(this, "추출된 물품:\n" + items.toString(), Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "인식된 물품이 없습니다.", Toast.LENGTH_SHORT).show();
        }
    }

    // 📌 권한 요청 결과 처리
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startCamera();
            } else {
                Toast.makeText(this, "카메라 권한이 필요합니다.", Toast.LENGTH_SHORT).show();
                finish();  // 권한 거부 시 액티비티 종료
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cameraExecutor.shutdown();
    }
}
