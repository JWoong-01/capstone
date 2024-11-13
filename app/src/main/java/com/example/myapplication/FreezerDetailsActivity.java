package com.example.myapplication;

import android.graphics.Rect;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.VolleyError;

import java.util.ArrayList;
import java.util.List;

public class FreezerDetailsActivity extends AppCompatActivity {
    private Button btn_fredge, btnDelete, btnPlus;
    private RecyclerView recyclerViewIngredients;
    private RecyclerView recyclerViewIngredientsFreezer;
    private IngredientAdapter ingredientAdapter;
    private IngredientAdapter freezerAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_freezer_details);

        // 레이아웃 요소 초기화
        recyclerViewIngredients = findViewById(R.id.recycler_view_ingredients_fridge);
        recyclerViewIngredientsFreezer = findViewById(R.id.recycler_view_ingredients_freezer);
        // RecyclerView 레이아웃 매니저 설정
        recyclerViewIngredients.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewIngredientsFreezer.setLayoutManager(new LinearLayoutManager(this));


        // 재료 리스트 불러오기
        loadIngredients();
        // btn_fredge 버튼 초기화
        btn_fredge = findViewById(R.id.btn_fredge);

        // btn_fredge 클릭 리스너 설정
        btn_fredge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FreezerDetailsActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });
    }


    private void loadIngredients() {
        ApiRequest apiRequest = new ApiRequest(this);
        apiRequest.fetchIngredients(new ApiRequest.IngredientFetchListener() {
            @Override
            public void onFetchSuccess(List<Ingredient> ingredients) {
                // 재료를 냉장실과 냉동실 리스트로 분리
                List<Ingredient> fridgeIngredients = new ArrayList<>();
                List<Ingredient> freezerIngredients = new ArrayList<>();

                for (Ingredient ingredient : ingredients) {
                    if ("냉동".equals(ingredient.getStorageLocation())) {
                        freezerIngredients.add(ingredient);
                    } else {
                        fridgeIngredients.add(ingredient);
                    }
                }

                // 냉장실과 냉동실 각각에 대한 어댑터 설정
                ingredientAdapter = new IngredientAdapter(fridgeIngredients, FreezerDetailsActivity.this);
                recyclerViewIngredients.setAdapter(ingredientAdapter);

                freezerAdapter = new IngredientAdapter(freezerIngredients, FreezerDetailsActivity.this);
                recyclerViewIngredientsFreezer.setAdapter(freezerAdapter);

                ingredientAdapter.notifyDataSetChanged();
                freezerAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFetchError(VolleyError error) {
                // 오류 발생 시 메시지 표시
                Toast.makeText(FreezerDetailsActivity.this, "재료를 불러오는 데 실패했습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }

}