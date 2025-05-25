package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class UserAddIngredientActivity extends AppCompatActivity {
    private EditText etIngredientName;
    private Button btnAddIngredient, btnBack, btnSetting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_add_ingredient_activity);

        Ingredient selectedIngredient = (Ingredient) getIntent().getSerializableExtra("ingredient");

        // UI 요소 초기화
        etIngredientName = findViewById(R.id.et_ingredient_name);
        btnAddIngredient = findViewById(R.id.btn_add_ingredient);
        btnBack = findViewById(R.id.btn_back);
        btnSetting = findViewById(R.id.btn_setting);

        if (selectedIngredient != null) {
            etIngredientName.setText(selectedIngredient.getName());
        }

        // 등록 버튼 클릭 리스너
        btnAddIngredient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 사용자 입력값 가져오기
                String ingredientName = etIngredientName.getText().toString().trim();

                if (ingredientName.isEmpty()) {
                    // 재료 이름이 비어 있으면 알림 표시
                    Toast.makeText(UserAddIngredientActivity.this, "재료 이름을 입력하세요.", Toast.LENGTH_SHORT).show();
                    return;
                }

                Intent intent = new Intent(UserAddIngredientActivity.this, AddDetailActivity.class);
                intent.putExtra("itemName", ingredientName);
                startActivity(intent);
            }
        });

        // 뒤로 가기 버튼
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserAddIngredientActivity.this, AddIngredientActivity.class);
                startActivity(intent);
            }
        });

        // 설정 버튼
        btnSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserAddIngredientActivity.this, SettingActivity.class);
                startActivity(intent);
            }
        });
    }
}