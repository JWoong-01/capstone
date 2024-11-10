package com.example.myapplication;

import android.graphics.Rect;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;

import androidx.appcompat.app.  AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.VolleyError;

import java.util.List;

public class HomeActivity extends AppCompatActivity {
    private Button btnEdit, btnDelete, btnPlus;
    private TextView tvTitle;
    private RecyclerView recyclerViewIngredients;
    private IngredientAdapter ingredientAdapter;
    private boolean isDeleteMode = false;  // 삭제 모드 상태


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
        recyclerViewIngredients.setLayoutManager(new GridLayoutManager(this,4));
        recyclerViewIngredients.addItemDecoration(new GridSpacingItemDecoration(4, 8, true));


        // 재료 리스트 불러오기
        loadIngredients();

        // 삭제 버튼 클릭 리스너 설정
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 삭제 모드를 토글
                isDeleteMode = !isDeleteMode;
                ingredientAdapter.setDeleteMode(isDeleteMode); // Adapter에 삭제 모드 설정
            }
        });

        // 추가 버튼 클릭 리스너 설정
        btnPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, AddIngredientActivity.class);
                startActivity(intent);
            }
        });
        TextView tvViewDetails = findViewById(R.id.tv_view_details);
        tvViewDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 상세보기 기능 구현
                // 예: 상세보기 페이지로 이동
                Intent intent = new Intent(HomeActivity.this, FreezerDetailsActivity.class);
                startActivity(intent);
            }
        });
    }
    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {
        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // 아이템의 위치
            int column = position % spanCount; // 현재 아이템이 위치한 열

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount;
                outRect.right = (column + 1) * spacing / spanCount;

                if (position < spanCount) { // 첫 번째 줄
                    outRect.top = spacing;
                }
                outRect.bottom = spacing;
            } else {
                outRect.left = column * spacing / spanCount;
                outRect.right = spacing - (column + 1) * spacing / spanCount;

                if (position >= spanCount) {
                    outRect.top = spacing;
                }
            }
        }
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