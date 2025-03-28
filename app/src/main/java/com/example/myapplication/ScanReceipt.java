package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class ScanReceipt extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scan_receipt);

        Button cancelButton = findViewById(R.id.btn_cancel);
        Button inputButton = findViewById(R.id.tab_input);


        // 취소 버튼 클릭
        cancelButton.setOnClickListener(v -> {
            Intent intent = new Intent(ScanReceipt.this, HomeActivity.class);
            startActivity(intent);
        });

        // 직접입력 버튼 클릭
        inputButton.setOnClickListener(v -> {
            Intent intent = new Intent(ScanReceipt.this, AddIngredientActivity.class);
            startActivity(intent);
        });

    }
}
