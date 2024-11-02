package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;;
import android.widget.Button;

public class HomeActivity extends AppCompatActivity {
    private Button btn_edit, btn_delete, btn_plus, btn_fredge, btn_recipe, btn_setting;
    private TextView tv_Title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        tv_Title = findViewById(R.id.tv_title);
        btn_edit = findViewById(R.id.btn_edit);
        btn_delete = findViewById(R.id.btn_delete);
        btn_plus = findViewById(R.id.btn_plus); // ID 수정
        btn_fredge = findViewById(R.id.btn_fredge);
        btn_recipe = findViewById(R.id.btn_recipe);
        btn_setting = findViewById(R.id.btn_setting);

        // Button 클릭 리스너 설정
        btn_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Edit 버튼의 동작 구현
            }
        });

        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Delete 버튼의 동작 구현
            }
        });

        btn_plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // AddIngredientActivity로 이동
                Intent intent = new Intent(HomeActivity.this, AddIngredientActivity.class);
                startActivity(intent);
            }
        });

        btn_fredge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Fridge 버튼의 동작 구현
            }
        });

        btn_recipe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Recipe 버튼의 동작 구현
            }
        });

        btn_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Setting 버튼의 동작 구현
            }
        });
    }
}