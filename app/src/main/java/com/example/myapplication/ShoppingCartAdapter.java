package com.example.myapplication;

import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShoppingCartAdapter extends RecyclerView.Adapter<ShoppingCartAdapter.ViewHolder> {

    private List<ShoppingItem> itemList;
    private final String[] unitArray = {"개", "봉지", "팩", "g", "ml"};
    private final Context context;
    private boolean isSelectionMode = false; // 선택 모드 상태

    public ShoppingCartAdapter(Context context, List<ShoppingItem> itemList) {
        this.context = context;
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


        // 선택 모드 상태에 따라 체크박스 보이기
        holder.checkBox.setVisibility(isSelectionMode ? View.VISIBLE : View.GONE);
        holder.checkBox.setChecked(item.isChecked());

        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            item.setChecked(isChecked);
        });

        // 항목 클릭 시 (선택 모드 아닐 때만 수정 다이얼로그 열기)
        holder.itemView.setOnClickListener(v -> {
            if (!isSelectionMode) {
                showEditDialog(item, position);
            } else {
                // 선택 모드에서는 클릭 시 체크 상태 토글
                holder.checkBox.setChecked(!holder.checkBox.isChecked());
            }
        });

        // 삭제 버튼
        holder.btnDelete.setOnClickListener(v -> deleteItemFromDatabase(item.getId(), position));
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvQuantity;
        ImageButton btnDelete;
        CheckBox checkBox;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_item_name);
            tvQuantity = itemView.findViewById(R.id.tv_item_quantity);
            btnDelete = itemView.findViewById(R.id.btn_delete);
            checkBox = new CheckBox(itemView.getContext());
            // 체크박스를 LinearLayout의 맨 앞에 동적으로 추가
            ((LinearLayout) itemView).addView(checkBox, 0);
        }
    }

    private void showEditDialog(ShoppingItem item, int position) {
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_add_item, null);

        AutoCompleteTextView etName = dialogView.findViewById(R.id.et_item_name);
        Button btnDecrease = dialogView.findViewById(R.id.btn_decrease);
        Button btnIncrease = dialogView.findViewById(R.id.btn_increase);
        TextView tvQuantity = dialogView.findViewById(R.id.tv_quantity);
        Spinner spinnerUnit = dialogView.findViewById(R.id.spinner_unit);

        etName.setText(item.getName());
        tvQuantity.setText(String.valueOf(item.getQuantity()));

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

        btnDecrease.setOnClickListener(v -> {
            int q = Integer.parseInt(tvQuantity.getText().toString());
            if (q > 1) tvQuantity.setText(String.valueOf(q - 1));
        });

        btnIncrease.setOnClickListener(v -> {
            int q = Integer.parseInt(tvQuantity.getText().toString());
            tvQuantity.setText(String.valueOf(q + 1));
        });

        new AlertDialog.Builder(context)
                .setTitle("품목 수정")
                .setView(dialogView)
                .setPositiveButton("저장", (dialog, which) -> {
                    String name = etName.getText().toString().trim();
                    String unit = spinnerUnit.getSelectedItem().toString();
                    int quantity = Integer.parseInt(tvQuantity.getText().toString());

                    if (!name.isEmpty()) {
                        item.setName(name);
                        item.setQuantity(quantity);
                        item.setUnit(unit);
                        notifyItemChanged(position);
                        updateItemInDatabase(item);
                    }
                })
                .setNegativeButton("취소", null)
                .show();
    }

    public void addShoppingItem(String name, int quantity, String unit, ApiRequest.ApiCallback callback) {
        String url = "http://yju407.dothome.co.kr/add_shopping_cart.php";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    Log.d("서버응답", "Raw response: " + response);
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.getBoolean("success")) {
                            int newId = jsonObject.getInt("id");
                            ShoppingItem newItem = new ShoppingItem(newId, name, quantity, unit);
                            itemList.add(newItem);
                            notifyItemInserted(itemList.size() - 1);
                            if (callback != null) callback.onSuccess(response);
                        } else {
                            Toast.makeText(context, "추가 실패: " + jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                            if (callback != null) callback.onError(new VolleyError("추가 실패"));
                        }
                    } catch (Exception e) {
                        Toast.makeText(context, "응답 파싱 오류", Toast.LENGTH_SHORT).show();
                        if (callback != null) callback.onError(new VolleyError(e));
                    }
                },
                error -> {
                    Toast.makeText(context, "서버 오류", Toast.LENGTH_SHORT).show();
                    if (callback != null) callback.onError(error);
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("name", name);
                params.put("quantity", String.valueOf(quantity));
                params.put("unit", unit);
                return params;
            }
        };

        Volley.newRequestQueue(context).add(stringRequest);
    }

    private void updateItemInDatabase(ShoppingItem item) {
        String url = "http://yju407.dothome.co.kr/update_shopping_cart.php";
        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {},
                error -> Toast.makeText(context, "수정 실패", Toast.LENGTH_SHORT).show()) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("id", String.valueOf(item.getId()));
                params.put("name", item.getName());
                params.put("quantity", String.valueOf(item.getQuantity()));
                params.put("unit", item.getUnit());
                return params;
            }
        };
        Volley.newRequestQueue(context).add(request);
    }

    public void deleteItemFromDatabase(int id, int position) {
        String url = "http://yju407.dothome.co.kr/delete_shopping_cart.php";

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    Log.d("서버응답", "Raw response: " + response);

                    try {
                        JSONObject json = new JSONObject(response);
                        boolean success = json.getBoolean("success");
                        if (success) {
                            itemList.remove(position);
                            notifyItemRemoved(position);
                            notifyItemRangeChanged(position, itemList.size());
                            Toast.makeText(context, "삭제되었습니다.", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, "삭제 실패: " + json.getString("message"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(context, "응답 파싱 오류", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    error.printStackTrace();
                    Toast.makeText(context, "서버 통신 오류", Toast.LENGTH_SHORT).show();
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("id", String.valueOf(id));
                return params;
            }
        };

        Volley.newRequestQueue(context).add(request);
    }

    // 선택 모드 상태 토글
    public void setSelectionMode(boolean enabled) {
        isSelectionMode = enabled;
        if (!enabled) {
            // 선택 해제
            for (ShoppingItem item : itemList) {
                item.setChecked(false);
            }
        }
        notifyDataSetChanged();
    }

    // 선택된 항목 리스트 반환
    public List<ShoppingItem> getSelectedItems() {
        return itemList.stream().filter(ShoppingItem::isChecked).toList();
    }
}
