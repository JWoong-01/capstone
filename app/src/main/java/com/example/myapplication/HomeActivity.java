package com.example.myapplication;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.VolleyError;

import java.util.List;

public class HomeActivity extends AppCompatActivity {
    private Button btnEdit, btnDelete, btnPlus;
    private TextView tvTitle;
    private RecyclerView recyclerViewIngredients;
    private IngredientAdapter ingredientAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // 레이아웃 요소 초기화
        tvTitle = findViewById(R.id.tv_title);
        btnEdit = findViewById(R.id.btn_edit);
        btnDelete = findViewById(R.id.btn_delete);
        btnPlus = findViewById(R.id.btn_plus);
        recyclerViewIngredients = findViewById(R.id.recycler_view_ingredients);
        // RecyclerView 레이아웃 매니저 설정
        recyclerViewIngredients.setLayoutManager(new LinearLayoutManager(this));

        // 재료 리스트 불러오기
        loadIngredients();

        // 추가 버튼 클릭 리스너 설정 (이 기능은 추후 adddetail로 이동해야함, addingrement로 페이지 이동만 하게 수정)
        btnPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // AddDetailActivity로 이동
                Intent intent = new Intent(HomeActivity.this, AddIngredientActivity.class);
                intent.putExtra("itemName", "양파"); // 재료 이름
                intent.putExtra("itemImage", R.drawable.it_onion); // 재료 이미지 리소스 ID
                startActivity(intent);
            }
        });
    }

    // 서버에서 재료 목록을 불러오는 메서드
    private void loadIngredients() {
        ApiRequest apiRequest = new ApiRequest(this);
        apiRequest.fetchIngredients(new ApiRequest.IngredientFetchListener() {
            @Override
            public void onFetchSuccess(List<Ingredient> ingredients) {
                // IngredientAdapter에 재료 목록을 설정
                ingredientAdapter = new IngredientAdapter(ingredients, HomeActivity.this);
                recyclerViewIngredients.setAdapter(ingredientAdapter);
                ingredientAdapter.notifyDataSetChanged();  // 데이터 변경 알리기

            }

            @Override
            public void onFetchError(VolleyError error) {
                // 오류 발생 시 메시지 표시
                Toast.makeText(HomeActivity.this, "재료를 불러오는 데 실패했습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
