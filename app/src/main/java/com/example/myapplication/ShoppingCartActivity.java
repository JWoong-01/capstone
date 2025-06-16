package com.example.myapplication;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.*;
import com.android.volley.toolbox.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.*;

public class ShoppingCartActivity extends AppCompatActivity {

    private ArrayList<ShoppingItem> shoppingList;
    private ShoppingCartAdapter adapter;
    private final String[] unitArray = {"개", "봉지", "팩", "g", "ml"};
    private final String SERVER_URL = "http://yju407.dothome.co.kr/get_shopping_cart.php";
    private final String ADD_INGREDIENTS_URL = "http://yju407.dothome.co.kr/add_ingredients_from_cart.php";

    private Button btnAddItem;
    private Button btnMoveToFridge;
    private boolean isSelectionMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_cart);

        Button btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(v -> finish());

        shoppingList = new ArrayList<>();
        adapter = new ShoppingCartAdapter(this, shoppingList);

        RecyclerView recyclerView = findViewById(R.id.recyclerViewShoppingCart);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        btnAddItem = findViewById(R.id.btn_add_item);
        btnAddItem.setOnClickListener(v -> showAddItemDialog());

        btnMoveToFridge = findViewById(R.id.btn_move_to_fridge);
        btnMoveToFridge.setOnClickListener(v -> {
            if (!isSelectionMode) {
                // 선택 모드 시작
                isSelectionMode = true;
                adapter.setSelectionMode(true);
                btnAddItem.setVisibility(View.GONE);
                btnMoveToFridge.setText("선택 항목 추가");
            } else {
                // 선택된 항목 서버에 전송
                List<ShoppingItem> selectedItems = adapter.getSelectedItems();
                if (selectedItems.isEmpty()) {
                    Toast.makeText(this, "선택된 항목이 없습니다.", Toast.LENGTH_SHORT).show();
                    return;
                }

                new AlertDialog.Builder(this)
                        .setTitle("내 냉장고에 추가")
                        .setMessage(selectedItems.size() + "개의 항목을 추가하시겠습니까?")
                        .setPositiveButton("추가", (dialog, which) -> addToFridge(selectedItems))
                        .setNegativeButton("취소", null)
                        .show();
            }
        });

        isSelectionMode = false;
        adapter.setSelectionMode(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchShoppingCartItems();
    }

    private void fetchShoppingCartItems() {
        shoppingList.clear();

        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest request = new StringRequest(Request.Method.GET, SERVER_URL,
                response -> {
                    Log.d("서버응답", "Raw response: " + response);

                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        JSONArray itemsArray = jsonResponse.getJSONArray("items");

                        for (int i = 0; i < itemsArray.length(); i++) {
                            JSONObject item = itemsArray.getJSONObject(i);
                            int id = item.getInt("id");
                            String name = item.getString("name");
                            int quantity = item.getInt("quantity");
                            String unit = item.getString("unit");

                            shoppingList.add(new ShoppingItem(id, name, quantity, unit));
                        }

                        adapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "파싱 오류 발생", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    error.printStackTrace();
                    Toast.makeText(this, "서버 연결 오류", Toast.LENGTH_SHORT).show();
                });

        queue.add(request);
    }

    private void showAddItemDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_item, null);

        AutoCompleteTextView etName = dialogView.findViewById(R.id.et_item_name);
        ImageButton btnDecrease = dialogView.findViewById(R.id.btn_decrease);
        ImageButton  btnIncrease = dialogView.findViewById(R.id.btn_increase);
        TextView tvQuantity = dialogView.findViewById(R.id.tv_quantity);
        Spinner spinnerUnit = dialogView.findViewById(R.id.spinner_unit);

        String[] itemSuggestions = {"우유", "계란", "감자", "사과", "양파", "당근"};
        ArrayAdapter<String> nameAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, itemSuggestions);
        etName.setAdapter(nameAdapter);

        ArrayAdapter<String> unitAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, unitArray);
        unitAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerUnit.setAdapter(unitAdapter);

        final int[] quantity = {1};
        tvQuantity.setText(String.valueOf(quantity[0]));

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

        new AlertDialog.Builder(this)
                .setTitle("장바구니 항목 추가")
                .setView(dialogView)
                .setPositiveButton("추가", (dialog, which) -> {
                    String name = etName.getText().toString().trim();
                    String unit = spinnerUnit.getSelectedItem().toString();

                    if (!name.isEmpty()) {
                        adapter.addShoppingItem(name, quantity[0], unit, null);
                    } else {
                        Toast.makeText(this, "품목명을 입력하세요.", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("취소", null)
                .show();
    }

    private void addToFridge(List<ShoppingItem> selectedItems) {
        JSONArray jsonArray = new JSONArray();
        try {
            for (ShoppingItem item : selectedItems) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("name", item.getName());
                jsonObject.put("quantity", item.getQuantity());
                jsonObject.put("unit", item.getUnit());
                jsonObject.put("storageLocation", "냉장실"); // 기본값
                jsonArray.put(jsonObject);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, "데이터 생성 오류", Toast.LENGTH_SHORT).show();
            return;
        }

        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest request = new StringRequest(Request.Method.POST, ADD_INGREDIENTS_URL,
                response -> {
                    Log.d("서버응답", response);
                    Toast.makeText(this, "선택된 항목을 냉장고에 추가했습니다!", Toast.LENGTH_SHORT).show();

                    // 선택 모드 종료
                    isSelectionMode = false;
                    adapter.setSelectionMode(false);
                    btnAddItem.setVisibility(View.VISIBLE);
                    btnMoveToFridge.setText("내 냉장고에 추가");
                },
                error -> {
                    error.printStackTrace();
                    Toast.makeText(this, "서버 연결 오류", Toast.LENGTH_SHORT).show();
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("items", jsonArray.toString());
                return params;
            }
        };

        queue.add(request);
    }
}