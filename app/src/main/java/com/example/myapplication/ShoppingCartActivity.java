package com.example.myapplication;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ShoppingCartActivity extends AppCompatActivity {

    private ArrayList<ShoppingItem> shoppingList;
    private ShoppingCartAdapter adapter;

    private final String[] unitArray = {"개", "봉지", "팩", "g", "ml"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_cart);

        shoppingList = new ArrayList<>();
        adapter = new ShoppingCartAdapter(shoppingList);

        RecyclerView recyclerView = findViewById(R.id.recyclerViewShoppingCart);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        Button btnAddItem = findViewById(R.id.btn_add_item);
        btnAddItem.setOnClickListener(v -> showAddItemDialog());
    }

    private void showAddItemDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_item, null);

        AutoCompleteTextView etName = dialogView.findViewById(R.id.et_item_name);
        Button btnDecrease = dialogView.findViewById(R.id.btn_decrease);
        Button btnIncrease = dialogView.findViewById(R.id.btn_increase);
        TextView tvQuantity = dialogView.findViewById(R.id.tv_quantity);
        Spinner spinnerUnit = dialogView.findViewById(R.id.spinner_unit);

        // 자동완성 품목 리스트
        String[] itemSuggestions = {"우유", "계란", "감자", "사과", "양파", "당근"};
        ArrayAdapter<String> nameAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, itemSuggestions);
        etName.setAdapter(nameAdapter);

        // 단위 선택 스피너
        ArrayAdapter<String> unitAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, unitArray);
        unitAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerUnit.setAdapter(unitAdapter);

        // 기본 수량
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
                        ShoppingItem newItem = new ShoppingItem(name, quantity[0], unit);
                        shoppingList.add(newItem);
                        adapter.notifyItemInserted(shoppingList.size() - 1);
                    } else {
                        Toast.makeText(this, "품목명을 입력하세요.", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("취소", null)
                .show();
    }
}
