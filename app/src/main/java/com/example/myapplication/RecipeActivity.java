package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.VolleyError;
import java.util.ArrayList;
import java.util.List;

public class RecipeActivity extends AppCompatActivity {

    private Button btn_fredge, btnSetting, btnRecipe;
    private RecyclerView recyclerViewRecipes;
    private RecipeAdapter recipeAdapter;
    private List<Recipe> recipeList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);

        btnSetting = findViewById(R.id.btn_setting);
        btnRecipe = findViewById(R.id.btn_recipe);
        btn_fredge = findViewById(R.id.btn_fredge);

        btn_fredge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RecipeActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });

        btnSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RecipeActivity.this, SettingActivity.class);
                startActivity(intent);
            }
        });

        btnRecipe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RecipeActivity.this, RecipeActivity.class);
                startActivity(intent);
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
            apiRequest.fetchRecipesFromXMLAPI(new ApiRequest.RecipeFetchListener() {
                @Override
                public void onFetchSuccess(List<Recipe> recipes) {
                    List<Recipe> filtered = filterRecipesByIngredients(recipes, ingredients);
                    recipeList.clear();
                    recipeList.addAll(filtered);
                    recipeAdapter.notifyDataSetChanged();
                }

                @Override
                public void onFetchError(VolleyError error) {
                    Toast.makeText(RecipeActivity.this, "공공 API 호출 실패", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(this, "재료 정보가 없습니다.", Toast.LENGTH_SHORT).show();
        }

        loadRecipes(ingredients);
    }


    private void loadRecipes(List<String> ingredients) {
        ApiRequest apiRequest = new ApiRequest(this);
        apiRequest.fetchRecipesByIngredients(ingredients, new ApiRequest.RecipeFetchListener() {
            @Override
            public void onFetchSuccess(List<Recipe> recipes) {
                // 재료 목록을 기준으로 필터링
                List<Recipe> filteredRecipes = filterRecipesByIngredients(recipes, ingredients);

                if (filteredRecipes.isEmpty()) {
                    // 겹치는 재료가 없으면 메시지 표시
                    Toast.makeText(RecipeActivity.this, "겹치는 재료가 없습니다. 재료를 추가하세요!", Toast.LENGTH_LONG).show();
                } else {
                    // 겹치는 재료가 있는 레시피만 표시
                    recipeList.clear();
                    recipeList.addAll(filteredRecipes);
                    recipeAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFetchError(VolleyError error) {
                Toast.makeText(RecipeActivity.this, "레시피를 불러오는 데 실패했습니다.", Toast.LENGTH_SHORT).show();
            }
        });
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