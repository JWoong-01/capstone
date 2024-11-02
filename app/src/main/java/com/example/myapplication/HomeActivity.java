package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;


import com.android.volley.VolleyError;

import java.util.List;

public class HomeActivity extends AppCompatActivity {
    private Button btnEdit, btnDelete, btnPlus;
    private TextView tvTitle, tvIngredients;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        tvTitle = findViewById(R.id.tv_title);
        btnEdit = findViewById(R.id.btn_edit);
        btnDelete = findViewById(R.id.btn_delete);
        btnPlus = findViewById(R.id.btn_plus);
        tvIngredients = findViewById(R.id.tv_ingredients); // 재료 표시할 TextView

        // 재료 불러오기
        loadIngredients();

        // 버튼 클릭 리스너 설정
        btnPlus.setOnClickListener(v -> {
            // AddIngredientActivity로 이동
            Intent intent = new Intent(HomeActivity.this, AddIngredientActivity.class);
            startActivity(intent);
        });

        // 나머지 버튼 클릭 리스너 추가...
    }

    private void loadIngredients() {
        ApiRequest apiRequest = new ApiRequest(this);
        apiRequest.fetchIngredients(new ApiRequest.IngredientFetchListener() {
            @Override
            public void onFetchSuccess(List<Ingredient> ingredients) {
                displayIngredients(ingredients);
            }

            @Override
            public void onFetchError(VolleyError error) {
                Toast.makeText(HomeActivity.this, "재료를 불러오는 데 실패했습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayIngredients(List<Ingredient> ingredients) {
        StringBuilder stringBuilder = new StringBuilder();
        for (Ingredient ingredient : ingredients) {
            stringBuilder.append(ingredient.getName())
                    .append(": ")
                    .append(ingredient.getQuantity())
                    .append("개, 유통기한: ")
                    .append(ingredient.getExpirationDate())
                    .append("\n");
        }
        tvIngredients.setText(stringBuilder.toString());
    }
}
