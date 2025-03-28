package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class UserInput extends AppCompatActivity {
    private EditText editInput;
    private Button btnCancel, btnSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_input);

        editInput = findViewById(R.id.edit_input);
        btnCancel = findViewById(R.id.btn_cancel);
        btnSave = findViewById(R.id.btn_save);

        // 취소 버튼 클릭
        btnCancel.setOnClickListener(v -> {
            Intent intent = new Intent(UserInput.this, HomeActivity.class);
            startActivity(intent);
            finish();
        });

        // 저장 버튼 클릭
        btnSave.setOnClickListener(v -> {
            String userInput = editInput.getText().toString();

            // AddDetail로 이동하면서 입력값 전달
            Intent intent = new Intent(UserInput.this, AddDetailActivity.class);
            intent.putExtra("user_input", userInput);
            startActivity(intent);
            finish();
        });
    }
}
