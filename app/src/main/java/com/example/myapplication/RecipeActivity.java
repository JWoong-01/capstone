package com.example.myapplication;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.VolleyError;
import java.util.ArrayList;
import java.util.List;

public class RecipeActivity extends AppCompatActivity {

    private RecyclerView recyclerViewRecipes;
    private RecipeAdapter recipeAdapter;
    private List<Recipe> recipeList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);

        // RecyclerView 초기화
        recyclerViewRecipes = findViewById(R.id.recycler_view_recipes);
        recyclerViewRecipes.setLayoutManager(new LinearLayoutManager(this));

        // 레시피 리스트 초기화
        recipeList = new ArrayList<>();
        recipeAdapter = new RecipeAdapter(recipeList, this);
        recyclerViewRecipes.setAdapter(recipeAdapter);

        // 홈 화면에서 전달된 재료 목록 받기
        ArrayList<String> ingredients = getIntent().getStringArrayListExtra("ingredients");

        // 레시피 데이터 불러오기
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
            // 레시피 재료 목록 (쉼표로 구분된 문자열)
            String[] recipeIngredients = recipe.getIngredients().split(",");
            List<String> recipeIngredientList = new ArrayList<>();

            // 레시피 재료의 각 항목에 대해 공백 제거 및 소문자로 변환
            for (String ingredient : recipeIngredients) {
                // 공백 제거하고 소문자 변환
                recipeIngredientList.add(ingredient.trim().toLowerCase());
            }

            // 사용자가 가진 재료 목록
            List<String> userIngredientList = new ArrayList<>();
            for (String ingredient : userIngredients) {
                // 공백 제거하고 소문자 변환
                userIngredientList.add(ingredient.trim().toLowerCase());
            }

            // 비교하기 전에 로그로 확인
            Log.d("RecipeFilter", "Recipe ingredients: " + recipeIngredientList);
            Log.d("RecipeFilter", "User ingredients: " + userIngredientList);

            // 겹치는 재료가 있는지 확인 (교집합)
            recipeIngredientList.retainAll(userIngredientList);

            // 교집합 결과를 로그로 출력
            Log.d("RecipeFilter", "Common ingredients: " + recipeIngredientList);

            // 겹치는 재료가 있다면 해당 레시피 추가
            if (!recipeIngredientList.isEmpty()) {
                filteredRecipes.add(recipe);
            }
        }

        return filteredRecipes;
    }


}
