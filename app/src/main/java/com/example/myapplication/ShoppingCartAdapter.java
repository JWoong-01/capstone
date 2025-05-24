package com.example.myapplication;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ShoppingCartAdapter extends RecyclerView.Adapter<ShoppingCartAdapter.ViewHolder> {

    private List<ShoppingItem> itemList;
    private String[] unitArray = {"개", "봉지", "팩", "g", "ml"};

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

        AutoCompleteTextView etName = dialogView.findViewById(R.id.et_item_name);
        Button btnDecrease = dialogView.findViewById(R.id.btn_decrease);
        Button btnIncrease = dialogView.findViewById(R.id.btn_increase);
        TextView tvQuantity = dialogView.findViewById(R.id.tv_quantity);
        Spinner spinnerUnit = dialogView.findViewById(R.id.spinner_unit);

        // 초기 값 설정
        etName.setText(item.getName());
        tvQuantity.setText(String.valueOf(item.getQuantity()));

        // Spinner 설정
        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, unitArray);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerUnit.setAdapter(adapter);

        int unitPosition = 0;
        for (int i = 0; i < unitArray.length; i++) {
            if (unitArray[i].equals(item.getUnit())) {
                unitPosition = i;
                break;
            }
        }
        spinnerUnit.setSelection(unitPosition);

        // 수량 조절 버튼
        final int[] quantity = {item.getQuantity()};

        btnDecrease.setOnClickListener(v -> {
            if (quantity[0] > 1) {
                quantity[0]--;
                tvQuantity.setText(String.valueOf(quantity[0]));
            }
        });

        btnIncrease.setOnClickListener(v -> {
            quantity[0]++;
            tvQuantity.setText(String.valueOf(quantity[0]));
        });

        new AlertDialog.Builder(context)
                .setTitle("품목 수정")
                .setView(dialogView)
                .setPositiveButton("저장", (dialog, which) -> {
                    String name = etName.getText().toString().trim();
                    String unit = spinnerUnit.getSelectedItem().toString();

                    if (!name.isEmpty()) {
                        item.setName(name);
                        item.setQuantity(quantity[0]);
                        item.setUnit(unit);
                        notifyItemChanged(position);
                    }
                })
                .setNegativeButton("취소", null)
                .show();
    }
}
