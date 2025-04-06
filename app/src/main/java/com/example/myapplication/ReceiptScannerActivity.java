package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import com.canhub.cropper.CropImage;
import com.canhub.cropper.CropImageContract;
import com.canhub.cropper.CropImageContractOptions;
import com.canhub.cropper.CropImageOptions;
import com.canhub.cropper.CropImageView;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.korean.KoreanTextRecognizerOptions;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ReceiptScannerActivity extends AppCompatActivity {
    private PreviewView cameraPreview;
    private ExecutorService cameraExecutor;
    private TextRecognizer textRecognizer;
    private ImageCapture imageCapture;

    private Uri capturedImageUri;

    private final ActivityResultLauncher<CropImageContractOptions> cropImageLauncher =
            registerForActivityResult(new CropImageContract(), result -> {
                if (result.isSuccessful()) {
                    Uri croppedUri = result.getUriContent();
                    try {
                        Bitmap croppedBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), croppedUri);
                        runOCR(croppedBitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    Exception error = result.getError();
                    error.printStackTrace();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receipt_scanner);

        cameraPreview = findViewById(R.id.preview_view);
        textRecognizer = TextRecognition.getClient(new KoreanTextRecognizerOptions.Builder().build());
        cameraExecutor = Executors.newSingleThreadExecutor();

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            startCamera();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CAMERA}, 1);
        }

        Button cancelButton = findViewById(R.id.btn_cancel);
        cancelButton.setOnClickListener(v -> {
            startActivity(new Intent(this, HomeActivity.class));
            finish();
        });

        Button scanTextButton = findViewById(R.id.button_barcord);
        scanTextButton.setOnClickListener(v -> {
            startActivity(new Intent(this, ScanReceipt.class));
            finish();
        });

        Button inputButton = findViewById(R.id.tab_input);
        inputButton.setOnClickListener(v -> {
            startActivity(new Intent(this, AddIngredientActivity.class));
            finish();
        });

        ImageButton captureButton = findViewById(R.id.button_capture);
        captureButton.setOnClickListener(v -> {
            if (imageCapture != null) {
                File photoFile = new File(getCacheDir(), "captured_image.jpg");

                ImageCapture.OutputFileOptions outputOptions =
                        new ImageCapture.OutputFileOptions.Builder(photoFile).build();

                imageCapture.takePicture(
                        outputOptions,
                        ContextCompat.getMainExecutor(this),
                        new ImageCapture.OnImageSavedCallback() {
                            @Override
                            public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                                Uri savedUri = Uri.fromFile(photoFile);
                                CropImageOptions cropOptions = new CropImageOptions();
                                cropOptions.guidelines = CropImageView.Guidelines.ON;
                                cropOptions.aspectRatioX = 4;
                                cropOptions.aspectRatioY = 3;
                                cropOptions.fixAspectRatio = true; // 비율 고정
                                cropOptions.activityMenuIconColor = Color.WHITE; // 메뉴 아이콘 색
                                cropOptions.outputCompressFormat = Bitmap.CompressFormat.JPEG;
                                cropOptions.outputCompressQuality = 90;

                                CropImageContractOptions options = new CropImageContractOptions(savedUri, cropOptions);
                                cropImageLauncher.launch(options);
                            }

                            @Override
                            public void onError(@NonNull ImageCaptureException exception) {
                                Toast.makeText(ReceiptScannerActivity.this, "촬영 실패: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                );
            }
        });
    }

    private void runOCR(Bitmap bitmap) {
        InputImage image = InputImage.fromBitmap(bitmap, 0);
        textRecognizer.process(image)
                .addOnSuccessListener(visionText -> {
                    String rawText = visionText.getText();
                    Log.d("OCR", "Recognized Text: " + rawText);

                    String[] lines = rawText.split("\n");
                    List<String> itemList = new ArrayList<>();

                    // 제외할 키워드
                    List<String> excludeKeywords = Arrays.asList(
                            "영수증", "합계", "결제", "할인", "포인트", "마트", "세제", "비누",
                            "면세", "카드", "현금", "잔돈", "부가세", "거스름돈", "총액", "금액", "소계"
                    );

                    for (String line : lines) {
                        String trimmed = line.trim();
                        if (trimmed.isEmpty()) continue;

                        // 숫자가 포함된 줄만 검사
                        boolean hasNumber = trimmed.matches(".*\\d+.*");

                        // 제외 키워드 포함 여부 검사
                        boolean isExcluded = false;
                        for (String keyword : excludeKeywords) {
                            if (trimmed.contains(keyword)) {
                                isExcluded = true;
                                break;
                            }
                        }

                        if (hasNumber && !isExcluded) {
                            // 숫자/특수문자 제거 후 공백도 정리
                            String cleaned = trimmed.replaceAll("[\\d₩원,.()/\\[\\]{}<>~!@#^&*_=+|\\\\\":;?%`'•-]", "")
                                    .replaceAll("\\s+", " ")  // 여러 공백 → 한 칸
                                    .trim();

                            // 완전히 비어있지 않은 텍스트만 추가
                            if (!cleaned.isEmpty()) {
                                itemList.add(cleaned);
                            }
                        }
                    }

                    if (!itemList.isEmpty()) {
                        Intent intent = new Intent(this, ReceiptCheckActivity.class);
                        intent.putStringArrayListExtra("scanned_items", new ArrayList<>(itemList));
                        startActivity(intent);
                    } else {
                        Toast.makeText(this, "추출된 품목이 없습니다.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "OCR 실패: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(this);

        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                Preview preview = new Preview.Builder().build();
                preview.setSurfaceProvider(cameraPreview.getSurfaceProvider());

                CameraSelector cameraSelector = new CameraSelector.Builder()
                        .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                        .build();

                imageCapture = new ImageCapture.Builder().build();

                cameraProvider.unbindAll();
                cameraProvider.bindToLifecycle((LifecycleOwner) this, cameraSelector, preview, imageCapture);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }, ContextCompat.getMainExecutor(this));
    }

    private Uri getImageUri(Context context, Bitmap bitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), bitmap, "CapturedImage", null);
        return Uri.parse(path);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startCamera();
        } else {
            Toast.makeText(this, "카메라 권한이 필요합니다.", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cameraExecutor.shutdown();
    }
}