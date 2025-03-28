package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

public class RecipeDetailActivity extends AppCompatActivity {
    private Button btn_fredge, btnSetting,btnRecipe;
    private TextView RecipeName, IngredientsTitle, IngredientsList, CookingInstructionsTitle, CookingInstructions;
    private ImageView RecipeImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        btnSetting = findViewById(R.id.btn_setting);
        btnRecipe = findViewById(R.id.btn_recipe);
        btn_fredge = findViewById(R.id.btn_fredge);

        btn_fredge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RecipeDetailActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });

        btnSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RecipeDetailActivity.this, SettingActivity.class);
                startActivity(intent);
            }
        });

        btnRecipe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RecipeDetailActivity.this, RecipeActivity.class);
                startActivity(intent);
            }
        });

        // Intent로부터 데이터 받음
        String recipeName = getIntent().getStringExtra("RECIPE_NAME");
        String recipeImage = getIntent().getStringExtra("RECIPE_IMAGE"); // 기본 이미지
        String ingredients = getIntent().getStringExtra("INGREDIENTS"); // 재료를 하나의 문자열로 받음
        String cookingInstructions = getIntent().getStringExtra("COOKING_INSTRUCTIONS"); // 조리 방법도 문자열로 받음

        // 레시피 이름
        RecipeName = findViewById(R.id.recipe_name);
        RecipeName.setText(recipeName);
        RecipeName.setTextSize(20);
        RecipeName.setTextAlignment(TextView.TEXT_ALIGNMENT_CENTER); // 가운데 정렬

        // 레시피 이미지
        RecipeImage = findViewById(R.id.recipe_image);
        if (recipeImage != null && !recipeImage.isEmpty()) {
            Glide.with(this)
                    .load(recipeImage) // URL에서 이미지 로드
                    .error(R.drawable.ic_recipe) // 에러 시 표시할 이미지
                    .into(RecipeImage);
        } else {
            RecipeImage.setImageResource(R.drawable.ic_recipe); // 기본 이미지 설정
        }

        // 재료 목록 타이틀
        IngredientsTitle = findViewById(R.id.ingredients_title);
        IngredientsTitle.setText("재료 목록");
        IngredientsTitle.setTextSize(18);
        IngredientsTitle.setTextAlignment(TextView.TEXT_ALIGNMENT_CENTER);

        // 재료 목록 (문자열로 설정)
        IngredientsList = findViewById(R.id.ingredients_list);
        IngredientsList.setText(ingredients);
        IngredientsList.setTextSize(16);

        // 조리 방법 타이틀
        CookingInstructionsTitle = findViewById(R.id.cooking_instructions_title);
        CookingInstructionsTitle.setText("조리 방법");
        CookingInstructionsTitle.setTextSize(18);
        CookingInstructionsTitle.setTextAlignment(TextView.TEXT_ALIGNMENT_CENTER);

        // 조리 방법 (문자열로 설정)
        CookingInstructions = findViewById(R.id.cooking_instructions);
        CookingInstructions.setText(cookingInstructions);
        CookingInstructions.setTextSize(16);
    }
}
