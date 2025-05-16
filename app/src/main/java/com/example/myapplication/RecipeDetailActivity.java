package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;

public class RecipeDetailActivity extends AppCompatActivity {
    private Button btn_fredge, btnSetting, btnRecipe, btnToggleImages;
    private TextView RecipeName, IngredientsTitle, IngredientsList, CookingInstructionsTitle;
    private ImageView RecipeImage;
    private LinearLayout cookingInstructionContainer;
    private boolean isInstructionsLoaded = false; // 이미 로딩했는지 체크

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        // 네비게이션 버튼
        btnSetting = findViewById(R.id.btn_setting);
        btnRecipe = findViewById(R.id.btn_recipe);
        btn_fredge = findViewById(R.id.btn_fredge);

        btn_fredge.setOnClickListener(v -> startActivity(new Intent(this, HomeActivity.class)));
        btnSetting.setOnClickListener(v -> startActivity(new Intent(this, SettingActivity.class)));
        btnRecipe.setOnClickListener(v -> startActivity(new Intent(this, RecipeActivity.class)));

        // 데이터
        String recipeName = getIntent().getStringExtra("RECIPE_NAME");
        String recipeImage = getIntent().getStringExtra("RECIPE_IMAGE");
        String ingredients = getIntent().getStringExtra("INGREDIENTS");
        String cookingInstructions = getIntent().getStringExtra("COOKING_INSTRUCTIONS");

        // 텍스트 세팅
        RecipeName = findViewById(R.id.recipe_name);
        RecipeName.setText(recipeName);
        RecipeName.setTextSize(20);
        RecipeName.setTextAlignment(TextView.TEXT_ALIGNMENT_CENTER);

        IngredientsTitle = findViewById(R.id.ingredients_title);
        IngredientsTitle.setText("재료 목록");
        IngredientsTitle.setTextSize(18);
        IngredientsTitle.setTextAlignment(TextView.TEXT_ALIGNMENT_CENTER);

        IngredientsList = findViewById(R.id.ingredients_list);
        IngredientsList.setText(ingredients);
        IngredientsList.setTextSize(16);

        // 이미지 세팅
        RecipeImage = findViewById(R.id.recipe_image);
        if (recipeImage != null && !recipeImage.isEmpty()) {
            Glide.with(this).load(recipeImage).error(R.drawable.ic_recipe).into(RecipeImage);
        } else {
            RecipeImage.setImageResource(R.drawable.ic_recipe);
        }

        // 조리 방법
        CookingInstructionsTitle = findViewById(R.id.cooking_instructions_title);
        CookingInstructionsTitle.setText("조리 방법");
        CookingInstructionsTitle.setTextSize(18);
        CookingInstructionsTitle.setTextAlignment(TextView.TEXT_ALIGNMENT_CENTER);

        // 이미지보는 버튼
        btnToggleImages = findViewById(R.id.btn_toggle_images);
        cookingInstructionContainer = findViewById(R.id.cooking_instruction_container);
        cookingInstructionContainer.setVisibility(View.VISIBLE);

        if (cookingInstructions != null && !cookingInstructions.isEmpty()) {
            String[] lines = cookingInstructions.split("\n");

            for (String line : lines) {
                line = line.trim();
                if (!line.startsWith("http")) {
                    TextView textView = new TextView(this);
                    textView.setText(line);
                    textView.setTextSize(16);
                    LinearLayout.LayoutParams txtParams = new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT
                    );
                    txtParams.setMargins(0, 8, 0, 8);
                    textView.setLayoutParams(txtParams);
                    cookingInstructionContainer.addView(textView);
                }
            }
        }

// 이미지는 + 버튼 눌렀을 때만 추가
        btnToggleImages.setOnClickListener(v -> {
            if (!isInstructionsLoaded && cookingInstructions != null) {
                String[] lines = cookingInstructions.split("\n");

                for (String line : lines) {
                    line = line.trim();
                    if (line.startsWith("http")) {
                        ImageView imageView = new ImageView(this);
                        LinearLayout.LayoutParams imgParams = new LinearLayout.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT
                        );
                        imgParams.setMargins(0, 16, 0, 16);
                        imageView.setLayoutParams(imgParams);
                        imageView.setAdjustViewBounds(true);
                        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);

                        Glide.with(this)
                                .load(line)
                                .placeholder(R.drawable.ic_recipe)
                                .error(R.drawable.ic_recipe)
                                .into(imageView);

                        cookingInstructionContainer.addView(imageView);
                    }
                }

                isInstructionsLoaded = true;
                btnToggleImages.setVisibility(View.GONE);
            }
        });
    }
}