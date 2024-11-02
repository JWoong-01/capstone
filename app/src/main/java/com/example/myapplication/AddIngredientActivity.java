package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

public class AddIngredientActivity extends AppCompatActivity {
    private Button btn_fredge,btn_add_onion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_ingredient);

        // btn_fredge 버튼 초기화
        btn_fredge = findViewById(R.id.btn_fredge);
        ImageButton btn_add_onion = findViewById(R.id.btn_add_onion);

        // btn_fredge 클릭 리스너 설정
        btn_fredge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddIngredientActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });

        btn_add_onion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // AddDetailActivity로 이동
                Intent intent = new Intent(AddIngredientActivity.this, AddDetailActivity.class);
                intent.putExtra("itemName", "양파"); // 재료 이름
                intent.putExtra("itemImage", R.drawable.it_onion); // 재료 이미지 리소스 ID
                startActivity(intent);
            }
        });

    }
}
