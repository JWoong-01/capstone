package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder> {

    private List<Recipe> recipeList;
    private Context context;

    // Constructor
    public RecipeAdapter(List<Recipe> recipeList, Context context) {
        this.recipeList = recipeList;
        this.context = context;
    }

    @NonNull
    @Override
    public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // 레시피 항목의 레이아웃을 inflate
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recipe, parent, false);
        return new RecipeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeViewHolder holder, int position) {
        // 데이터 바인딩: 각 레시피 객체의 데이터를 뷰에 표시
        Recipe recipe = recipeList.get(position);

        // 레시피 제목 설정
        holder.recipeTitle.setText(recipe.getName());

        // 레시피 재료
        holder.recipeingredients.setText("재료: " + recipe.getIngredients());


        // 이미지 URL을 Glide로 로드
        Glide.with(context)
                .load(recipe.getImageUrl())  // 이미지 URL
                .into(holder.recipeImage);   // ImageView에 설정

        // 클릭 리스너 설정
        holder.itemView.setOnClickListener(v -> {
            // 레시피의 세부 정보를 RecipeDetailActivity로 넘김ㅁ
            Intent intent = new Intent(context, RecipeDetailActivity.class);
            intent.putExtra("RECIPE_NAME", recipe.getName());
            intent.putExtra("RECIPE_IMAGE", recipe.getImageUrl());
            intent.putExtra("INGREDIENTS", recipe.getIngredients()); // 재료 목록
            intent.putExtra("COOKING_INSTRUCTIONS", recipe.getInstructions());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return recipeList.size();
    }

    public static class RecipeViewHolder extends RecyclerView.ViewHolder {
        TextView recipeTitle, recipeingredients;
        ImageView recipeImage;

        public RecipeViewHolder(View itemView) {
            super(itemView);
            recipeTitle = itemView.findViewById(R.id.recipe_title);
            recipeingredients = itemView.findViewById(R.id.recipe_ingredients);
            recipeImage = itemView.findViewById(R.id.recipe_image);
        }
    }

    // 데이터가 변경되었을 때 어댑터에 알려주는 메서드
    public void updateRecipeList(List<Recipe> newRecipes) {
        this.recipeList = newRecipes;
        notifyDataSetChanged();
    }
}
