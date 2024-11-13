package com.example.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class IngredientAdapter extends RecyclerView.Adapter<IngredientAdapter.ViewHolder> {

    private List<Ingredient> ingredients;
    private Context context;
    private ApiRequest apiRequest;
    private boolean isDeleteMode = false;  // 삭제 모드 상태


    // Constructor
    public IngredientAdapter(List<Ingredient> ingredients, Context context) {
        this.ingredients = ingredients;
        this.context = context;
        apiRequest = new ApiRequest(context); // ApiRequest 인스턴스 생성
    }
    public void setDeleteMode(boolean isDeleteMode) {
        this.isDeleteMode = isDeleteMode;
        notifyDataSetChanged();  // 삭제 모드가 변경되었으므로 Adapter를 새로고침
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Activity에 따라 다른 레이아웃을 선택
        View view;
        if (context instanceof HomeActivity) {
            view = LayoutInflater.from(context).inflate(R.layout.item_ingredient, parent, false);
        } else {
            view = LayoutInflater.from(context).inflate(R.layout.item_ingredient_detail, parent, false);
        }
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // 해당 position의 재료 데이터를 가져옴
        Ingredient ingredient = ingredients.get(position);
        String expirationDateString = ingredient.getFormattedExpirationDate();

        // 재료 이름, 수량, 유통기한 등을 표시
        holder.nameTextView.setText(ingredient.getName());
        holder.quantityTextView.setText(ingredient.getQuantity() + "개");
        holder.intakeDate.setText("입고날짜 "+ ingredient.getIntakeDate());
        holder.expirationDateTextView.setText("유통기한: " + ingredient.getFormattedExpirationDate());
        int imageResId = ingredient.getImageResId();
        holder.imageView.setImageResource(imageResId);
        // D-Day 계산 및 표시
        String dDayText = ingredient.calculateDDay();
        holder.dDayTextView.setText(dDayText); // dDayTextView에 D-day 설정

        // 삭제 모드일 때만 삭제 버튼을 보여줌
        if (isDeleteMode) {
            holder.deleteButton.setVisibility(View.VISIBLE);
        } else {
            holder.deleteButton.setVisibility(View.GONE);
        }

        // 삭제 버튼 클릭 시 서버에 삭제 요청
        holder.deleteButton.setOnClickListener(v -> {
            int currentPosition = holder.getAdapterPosition();  // 현재 아이템의 위치 가져오기
            if (currentPosition != RecyclerView.NO_POSITION) {
                // 서버에 이미지 리소스를 기준으로 삭제 요청
                apiRequest.deleteIngredientByImage(imageResId, new ApiRequest.ApiDeleteListener() {
                    @Override
                    public void onDeleteSuccess() {
                        // 삭제 성공 시 해당 아이템을 목록에서 제거
                        ingredients.remove(currentPosition);
                        notifyItemRemoved(currentPosition);
                        notifyItemRangeChanged(currentPosition, ingredients.size());
                    }

                    @Override
                    public void onDeleteError() {
                        // 오류 발생 시 사용자에게 알림
                        Toast.makeText(context, "삭제 실패", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        // 수정 버튼 클릭 시 수정 화면으로 이동
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, EditIngredientActivity.class);
            intent.putExtra("ingredient", ingredient);  // Ingredient 객체를 전달
            context.startActivity(intent);
        });
    }


    @Override
    public int getItemCount() {
        return ingredients.size();
    }


    // ViewHolder 클래스
    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView nameTextView, quantityTextView, intakeDate, expirationDateTextView, dDayTextView;
        public ImageView imageView;
        Button editButton, deleteButton;

        public ViewHolder(View itemView) {
            super(itemView);
            // 각 아이템 뷰의 레퍼런스를 초기화
            nameTextView = itemView.findViewById(R.id.ingredient_name);
            quantityTextView = itemView.findViewById(R.id.ingredient_quantity);
            intakeDate = itemView.findViewById(R.id.ingredient_intake_date);
            expirationDateTextView = itemView.findViewById(R.id.ingredient_expiration_date);
            imageView = itemView.findViewById(R.id.ingredient_image);
            editButton = itemView.findViewById(R.id.btn_edit);
            deleteButton = itemView.findViewById(R.id.btn_delete);
            dDayTextView = itemView.findViewById(R.id.tv_d_day);  // dDayTextView 초기화

        }
    }
}
