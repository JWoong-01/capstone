
package com.example.myapplication;

import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.VolleyError;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {
    private static final int NOTIFICATION_PERMISSION_REQUEST_CODE = 1001;
    private Button btnEdit, btnDelete, btnPlus, btnSetting, btnRecipe, btnRecipeView;
    private TextView tvTitle;
    private RecyclerView recyclerViewIngredients;
    private RecyclerView recyclerViewIngredientsFreezer;
    private IngredientAdapter ingredientAdapter;
    private IngredientAdapter freezerAdapter;
    private boolean isDeleteMode = false;  // 삭제 모드 상태

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Android 13 이상에서 알림 권한 요청
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, NOTIFICATION_PERMISSION_REQUEST_CODE);
        }

        // 레이아웃 요소 초기화
        tvTitle = findViewById(R.id.tv_title);
        btnDelete = findViewById(R.id.btn_delete);
        btnPlus = findViewById(R.id.btn_plus);
        btnSetting = findViewById(R.id.btn_setting);
        btnRecipe = findViewById(R.id.btn_recipe);
        btnRecipeView = findViewById(R.id.btn_recipe_view);
        recyclerViewIngredients = findViewById(R.id.recycler_view_ingredients_fridge);
        recyclerViewIngredientsFreezer = findViewById(R.id.recycler_view_ingredients_freezer);

        // RecyclerView 레이아웃 매니저 설정
        recyclerViewIngredients.setLayoutManager(new GridLayoutManager(this, 4));
        recyclerViewIngredients.addItemDecoration(new GridSpacingItemDecoration(4, 8, true));

        recyclerViewIngredientsFreezer.setLayoutManager(new GridLayoutManager(this, 4));
        recyclerViewIngredientsFreezer.addItemDecoration(new GridSpacingItemDecoration(4, 8, true));

        // 재료 리스트 불러오기
        loadIngredients();

        // 삭제 버튼 클릭 리스너 설정
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 삭제 모드를 토글
                isDeleteMode = !isDeleteMode;
                ingredientAdapter.setDeleteMode(isDeleteMode); // 냉장실 어댑터에 삭제 모드 설정
                freezerAdapter.setDeleteMode(isDeleteMode);   // 냉동실 어댑터에 삭제 모드 설정
            }
        });

        // 설정 버튼 클릭 리스너
        btnSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, SettingActivity.class);
                startActivity(intent);
            }
        });

        // 레시피 찾기 버튼 클릭 리스너(모든 레시피)
        btnRecipe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, RecipeInfoActivity.class);
                startActivity(intent);
            }
        });

        // 레시피 보기 버튼 클릭 리스너
        btnRecipeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 냉장고와 냉동실에 있는 재료들 추출
                List<String> ingredients = getIngredientsFromRecyclerViews();

                Intent intent = new Intent(HomeActivity.this, RecipeActivity.class);
                intent.putStringArrayListExtra("ingredients", (ArrayList<String>) ingredients); // 재료 목록 전달
                startActivity(intent);
            }
        });

        // 추가 버튼 클릭 리스너 설정
        btnPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, ScanReceipt.class);
                startActivity(intent);
            }
        });

        TextView tvViewDetails = findViewById(R.id.tv_view_details);
        tvViewDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 상세보기 기능 구현
                Intent intent = new Intent(HomeActivity.this, FreezerDetailsActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == NOTIFICATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 권한이 승인됨 - 알림을 보낼 수 있음
            } else {
                // 권한이 거부됨
            }
        }
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

    // 저장된 재료들을 RecyclerView에서 추출하는 메서드
    private List<String> getIngredientsFromRecyclerViews() {
        List<String> ingredients = new ArrayList<>();

        // 냉장고와 냉동실에 있는 재료들 추출
        if (ingredientAdapter != null) {
            for (Ingredient ingredient : ingredientAdapter.getIngredients()) {
                ingredients.add(ingredient.getName());
            }
        }

        if (freezerAdapter != null) {
            for (Ingredient ingredient : freezerAdapter.getIngredients()) {
                ingredients.add(ingredient.getName());
            }
        }

        return ingredients;
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
                ingredientAdapter = new IngredientAdapter(fridgeIngredients, HomeActivity.this);
                recyclerViewIngredients.setAdapter(ingredientAdapter);

                freezerAdapter = new IngredientAdapter(freezerIngredients, HomeActivity.this);
                recyclerViewIngredientsFreezer.setAdapter(freezerAdapter);

                ingredientAdapter.notifyDataSetChanged();
                freezerAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFetchError(VolleyError error) {
                // 오류 발생 시 메시지 표시
                Toast.makeText(HomeActivity.this, "재료를 불러오는 데 실패했습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
