package com.example.myapplication;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ShoppingCartAdapter extends RecyclerView.Adapter<ShoppingCartAdapter.ViewHolder> {

    private List<ShoppingItem> itemList;

    public ShoppingCartAdapter(List<ShoppingItem> itemList) {
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public ShoppingCartAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_shopping_cart, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ShoppingCartAdapter.ViewHolder holder, int position) {
        ShoppingItem item = itemList.get(position);
        holder.tvName.setText(item.getName());
        holder.tvQuantity.setText(item.getQuantity() + " " + item.getUnit());

        // 항목 클릭 시 수정 다이얼로그 띄우기
        holder.itemView.setOnClickListener(v -> {
            showEditDialog(holder.itemView.getContext(), item, position);
        });

        // 삭제 버튼 클릭 시 항목 제거
        holder.btnDelete.setOnClickListener(v -> {
            itemList.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, itemList.size());
        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvQuantity;
        ImageButton btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_item_name);
            tvQuantity = itemView.findViewById(R.id.tv_item_quantity);
            btnDelete = itemView.findViewById(R.id.btn_delete);
        }
    }

    private void showEditDialog(Context context, ShoppingItem item, int position) {
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_add_item, null);
        EditText etName = dialogView.findViewById(R.id.et_item_name);
        EditText etQuantity = dialogView.findViewById(R.id.et_item_quantity);
        EditText etUnit = dialogView.findViewById(R.id.et_item_unit);

        etName.setText(item.getName());
        etQuantity.setText(String.valueOf(item.getQuantity()));
        etUnit.setText(item.getUnit());

        new AlertDialog.Builder(context)
                .setTitle("품목 수정")
                .setView(dialogView)
                .setPositiveButton("저장", (dialog, which) -> {
                    String name = etName.getText().toString().trim();
                    String quantityStr = etQuantity.getText().toString().trim();
                    String unit = etUnit.getText().toString().trim();

                    if (!name.isEmpty() && !quantityStr.isEmpty()) {
                        int quantity = Integer.parseInt(quantityStr);
                        item.setName(name);
                        item.setQuantity(quantity);
                        item.setUnit(unit);
                        notifyItemChanged(position);
                    }
                })
                .setNegativeButton("취소", null)
                .show();
    }
}
