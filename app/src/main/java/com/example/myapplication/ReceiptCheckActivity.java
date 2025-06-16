package com.example.myapplication;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ReceiptCheckActivity extends AppCompatActivity {
    private List<String> scannedItems;
    private ReceiptItemAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_receipt); // RecyclerView 포함된 레이아웃

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        Button saveButton = findViewById(R.id.btn_save);

        scannedItems = getIntent().getStringArrayListExtra("scanned_items");
        if (scannedItems == null) scannedItems = new ArrayList<>();

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ReceiptItemAdapter(scannedItems);
        recyclerView.setAdapter(adapter);

        saveButton.setOnClickListener(v -> {
            if (getCurrentFocus() != null) {
                getCurrentFocus().clearFocus();
            }

            saveSelectedItemsToDB();
        });
    }
    private void saveSelectedItemsToDB() {
        List<ReceiptItemAdapter.ReceiptItem> selectedItems = adapter.getSelectedItems();

        if (selectedItems.isEmpty()) {
            Toast.makeText(this, "선택된 품목이 없습니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        ApiRequest apiRequest = new ApiRequest(this);

        for (ReceiptItemAdapter.ReceiptItem item : selectedItems) {
            Calendar today = Calendar.getInstance();
            String intakeDate = String.format("%d-%02d-%02d",
                    today.get(Calendar.YEAR), today.get(Calendar.MONTH) + 1, today.get(Calendar.DAY_OF_MONTH));

            today.add(Calendar.DAY_OF_MONTH, 7);
            String expirationDate = String.format("%d-%02d-%02d",
                    today.get(Calendar.YEAR), today.get(Calendar.MONTH) + 1, today.get(Calendar.DAY_OF_MONTH));

            String matchedName = IngredientData.getMatchedKoreanName(item.name);
            int imageResId = IngredientData.getImageResource(matchedName);

            // 저장 시 이름, 수량, 단위 포함해서 전송
            apiRequest.addIngredient(
                    matchedName,
                    item.quantity,
                    item.unit,             // ✅ String unit
                    intakeDate,
                    expirationDate,
                    "냉장",                // ✅ storageLocation
                    imageResId
            );

            Toast.makeText(this, "품목이 저장되었습니다.", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}