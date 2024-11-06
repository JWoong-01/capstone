package com.example.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class IngredientAdapter extends RecyclerView.Adapter<IngredientAdapter.ViewHolder> {

    private List<Ingredient> ingredients;
    private Context context;

    // Constructor
    public IngredientAdapter(List<Ingredient> ingredients, Context context) {
        this.ingredients = ingredients;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // 각 아이템의 레이아웃을 인플레이트
        View view = LayoutInflater.from(context).inflate(R.layout.item_ingredient, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // 해당 position의 재료 데이터를 가져옴
        Ingredient ingredient = ingredients.get(position);

        // 재료 이름, 수량, 유통기한 등을 표시
        holder.nameTextView.setText(ingredient.getName());
        holder.quantityTextView.setText(ingredient.getQuantity() + "개");
        holder.expirationDateTextView.setText("유통기한: " + ingredient.getExpirationDate());
        int imageResId = ingredient.getImageResId();
        holder.imageView.setImageResource(imageResId);
    }

    @Override
    public int getItemCount() {
        return ingredients.size();
    }

    // ViewHolder 클래스
    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView nameTextView, quantityTextView, expirationDateTextView;
        public ImageView imageView;

        public ViewHolder(View itemView) {
            super(itemView);
            // 각 아이템 뷰의 레퍼런스를 초기화
            nameTextView = itemView.findViewById(R.id.ingredient_name);
            quantityTextView = itemView.findViewById(R.id.ingredient_quantity);
            expirationDateTextView = itemView.findViewById(R.id.ingredient_expiration_date);
            imageView = itemView.findViewById(R.id.ingredient_image);
        }
    }
}
