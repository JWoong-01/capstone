package com.example.myapplication;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.VolleyError;
import java.util.ArrayList;
import java.util.List;

public class RecipeActivity extends AppCompatActivity {

    private Button btnBack;
    private RecyclerView recyclerViewRecipes;
    private RecipeAdapter recipeAdapter;
    private List<Recipe> recipeList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);

        // 뒤로 가기
        btnBack = findViewById(R.id.btn_back);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // RecyclerView 초기화
        recyclerViewRecipes = findViewById(R.id.recycler_view_recipes);
        recyclerViewRecipes.setLayoutManager(new LinearLayoutManager(this));

        // 레시피 리스트 초기화
        recipeList = new ArrayList<>();
        recipeAdapter = new RecipeAdapter(recipeList, this);
        recyclerViewRecipes.setAdapter(recipeAdapter);

        // 홈 화면에서 전달된 재료 목록 받기
        ArrayList<String> ingredients = getIntent().getStringArrayListExtra("ingredients");
        if (ingredients != null && !ingredients.isEmpty()) {
            ApiRequest apiRequest = new ApiRequest(this);
            apiRequest.fetchRecipesFromXMLAPI(new ApiRequest.RecipeFetchListener() {             //레시피 api 서버 오류 6/7~
                //   apiRequest.fetchRecipesFromLocalAsset(new ApiRequest.RecipeFetchListener() {            //대체 코드

                @Override
                public void onFetchSuccess(List<Recipe> recipes) {
                    List<Recipe> filtered = filterRecipesByIngredients(recipes, ingredients);
                    recipeList.clear();
                    recipeList.addAll(filtered);
                    recipeAdapter.notifyDataSetChanged();

                    if (filtered.isEmpty()) {
                        Toast.makeText(RecipeActivity.this, "보유하신 재료로 만들 수 있는 레시피가 없습니다!", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFetchError(VolleyError error) {
                    Toast.makeText(RecipeActivity.this, "공공 API 호출 실패", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(this, "재료 정보가 없습니다.", Toast.LENGTH_SHORT).show();
        }
    }

    // 재료를 비교하여 겹치는 레시피만 반환하는 메서드
    private List<Recipe> filterRecipesByIngredients(List<Recipe> recipes, List<String> userIngredients) {
        List<Recipe> filteredRecipes = new ArrayList<>();

        for (Recipe recipe : recipes) {
            String recipeIngredientText = recipe.getIngredients().toLowerCase();

            for (String userIngredient : userIngredients) {
                String keyword = userIngredient.trim().toLowerCase();

                if (recipeIngredientText.contains(keyword)) {
                    filteredRecipes.add(recipe);
                    break; // 하나라도 포함되면 추가
                }
            }
        }

        return filteredRecipes;
    }
}