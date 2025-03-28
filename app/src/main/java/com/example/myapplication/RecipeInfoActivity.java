package com.example.myapplication;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.VolleyError;
import java.util.ArrayList;
import java.util.List;

public class RecipeInfoActivity extends AppCompatActivity {

    private RecyclerView recyclerViewRecipes;
    private RecipeAdapter recipeAdapter;
    private List<Recipe> allRecipes; // 서버에서 받아온 전체 레시피 목록
    private List<Recipe> filteredRecipes; // 검색 결과로 필터링된 레시피 목록

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_info);

        // RecyclerView 설정
        recyclerViewRecipes = findViewById(R.id.recyclerViewRecipes);
        recyclerViewRecipes.setLayoutManager(new LinearLayoutManager(this));

        // 레시피 어댑터 설정
        filteredRecipes = new ArrayList<>();
        recipeAdapter = new RecipeAdapter(filteredRecipes, this);
        recyclerViewRecipes.setAdapter(recipeAdapter);

        // 검색바 설정
        SearchView searchView = findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterRecipes(query); // 레시피 필터링
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterRecipes(newText); // 레시피 필터링
                return false;
            }
        });

        // 레시피 목록 불러오기 (API 요청)
        loadRecipes();
    }

    // 레시피 목록을 가져오는 메서드
    private void loadRecipes() {
        ApiRequest apiRequest = new ApiRequest(this);
        apiRequest.fetchRecipesByIngredients(new ArrayList<>(), new ApiRequest.RecipeFetchListener() {
            @Override
            public void onFetchSuccess(List<Recipe> recipes) {
                allRecipes = recipes;
                filteredRecipes = new ArrayList<>(allRecipes);
                recipeAdapter.updateRecipeList(filteredRecipes); // 레시피 어댑터에 데이터 업데이트
                // 디버깅: 데이터가 올바르게 들어왔는지 확인
                for (Recipe recipe : recipes) {
                    Log.d("RecipeInfoActivity", "Recipe: " + recipe.getName());
                }
            }

            @Override
            public void onFetchError(VolleyError error) {
                Toast.makeText(RecipeInfoActivity.this, "레시피를 불러오는 데 실패했습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // 검색어에 맞는 레시피 목록을 필터링하는 메서드
    private void filterRecipes(String query) {
        filteredRecipes.clear();
        for (Recipe recipe : allRecipes) {
            if (recipe.getName().toLowerCase().contains(query.toLowerCase())) {
                filteredRecipes.add(recipe);
            }
        }

        recipeAdapter.updateRecipeList(filteredRecipes); // 필터링된 레시피를 어댑터에 업데이트
    }
}
