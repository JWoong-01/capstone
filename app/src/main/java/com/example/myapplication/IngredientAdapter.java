package com.example.myapplication;

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
import java.util.Calendar;

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
        Ingredient ingredient = ingredients.get(position);

        // 값 바인딩
        holder.nameTextView.setText(ingredient.getName());

        // 수량과 단위를 함께 보여줌
        holder.quantityWithUnitTextView.setText(ingredient.getQuantity() + ingredient.getUnit());

        holder.intakeDate.setText("입고날짜 " + ingredient.getIntakeDate());
        holder.expirationDateTextView.setText("유통기한: " + ingredient.getFormattedExpirationDate());

        // 이미지 설정

        //int imageResId = ingredient.getImageResId();
        //holder.imageView.setImageResource(imageResId);
        int imageResId = IngredientData.getImageResource(ingredient.getName());
        holder.imageView.setImageResource(imageResId);


        // D-Day 계산 및 표시
        String dDayText = ingredient.calculateDDay();
        holder.dDayTextView.setText(dDayText); // dDayTextView에 D-day 설정

        // D-Day 색상 변경
        Calendar today = Calendar.getInstance();
        long diffInMillis = ingredient.getExpirationDate().getTimeInMillis() - today.getTimeInMillis();
        long diffInDays = diffInMillis / (1000 * 60 * 60 * 24);

        if (diffInDays < 0) {
            holder.dDayTextView.setBackgroundResource(R.drawable.d_day_background_gray); // 지난 경우 - 회색
        } else if (diffInDays == 0) {
            holder.dDayTextView.setBackgroundResource(R.drawable.d_day_background_red); // 당일 - 빨강
        } else if (diffInDays <= 3) {
            holder.dDayTextView.setBackgroundResource(R.drawable.d_day_background_orange); // 3일 이하 - 주황
        } else if (diffInDays <= 7) {
            holder.dDayTextView.setBackgroundResource(R.drawable.d_day_background_green); // 7일 이상 - 초록
        }

        // 삭제 모드일 때만 삭제 버튼을 보여줌
        holder.deleteButton.setVisibility(isDeleteMode ? View.VISIBLE : View.GONE);

        // 삭제 버튼 클릭 시 서버에 삭제 요청
        holder.deleteButton.setOnClickListener(v -> {
            int currentPosition = holder.getAdapterPosition();  // 현재 아이템의 위치 가져오기
            if (currentPosition != RecyclerView.NO_POSITION) {
                apiRequest.deleteIngredientByImage(ingredient.getName(), new ApiRequest.ApiDeleteListener() {
                    @Override
                    public void onDeleteSuccess() {
                        ingredients.remove(currentPosition);
                        notifyItemRemoved(currentPosition);
                        notifyItemRangeChanged(currentPosition, ingredients.size());
                    }

                    @Override
                    public void onDeleteError() {
                        Toast.makeText(context, "삭제 실패", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        // 클릭 시 수정 액티비티로 이동
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, EditIngredientActivity.class);
            intent.putExtra("ingredient", ingredient);
            context.startActivity(intent);
        });
    }


    @Override
    public int getItemCount() {
        return ingredients.size();
    }

    // 재료 목록 반환
    public List<Ingredient> getIngredients() {
        return ingredients;
    }

    // ViewHolder 클래스
    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView nameTextView,  quantityWithUnitTextView,
                intakeDate, expirationDateTextView, dDayTextView;
        public ImageView imageView;
        public Button deleteButton;

        public ViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.ingredient_name);
            quantityWithUnitTextView = itemView.findViewById(R.id.ingredient_quantity_with_unit);
            intakeDate = itemView.findViewById(R.id.ingredient_intake_date);
            expirationDateTextView = itemView.findViewById(R.id.ingredient_expiration_date);
            imageView = itemView.findViewById(R.id.ingredient_image);
            deleteButton = itemView.findViewById(R.id.btn_delete);
            dDayTextView = itemView.findViewById(R.id.tv_d_day);
        }
    }
}