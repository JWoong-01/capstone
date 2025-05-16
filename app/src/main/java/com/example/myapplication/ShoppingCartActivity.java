package com.example.myapplication;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ShoppingCartActivity extends AppCompatActivity {

    private ArrayList<ShoppingItem> shoppingList;
    private ShoppingCartAdapter adapter;

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
        btnAddItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddItemDialog();
            }
        });
    }

    private void showAddItemDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_item, null);
        EditText etName = dialogView.findViewById(R.id.et_item_name);
        EditText etQuantity = dialogView.findViewById(R.id.et_item_quantity);
        EditText etUnit = dialogView.findViewById(R.id.et_item_unit);

        new AlertDialog.Builder(this)
                .setTitle("장바구니 항목 추가")
                .setView(dialogView)
                .setPositiveButton("추가", (dialog, which) -> {
                    String name = etName.getText().toString().trim();
                    String quantityStr = etQuantity.getText().toString().trim();
                    String unit = etUnit.getText().toString().trim();

                    if (!name.isEmpty() && !quantityStr.isEmpty() && !unit.isEmpty()) {
                        int quantity = Integer.parseInt(quantityStr);
                        ShoppingItem newItem = new ShoppingItem(name, quantity, unit);
                        shoppingList.add(newItem);
                        adapter.notifyItemInserted(shoppingList.size() - 1);
                    } else {
                        Toast.makeText(this, "모든 값을 입력하세요.", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("취소", null)
                .show();
    }
}
