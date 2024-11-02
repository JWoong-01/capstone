package com.example.myapplication;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.Calendar;
import androidx.appcompat.app.AppCompatActivity;

public class AddDetailActivity extends AppCompatActivity {

    private Button btnBack, btnDecreaseQuantity, btnIncreaseQuantity, btnIngredientAdd;
    private TextView tvItemName, quantityText;
    private ImageView ivItemImage;
    private EditText etExpirationDate;
    private int quantity = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_detail);

        // 뷰 초기화
        btnBack = findViewById(R.id.btn_back);
        btnDecreaseQuantity = findViewById(R.id.btn_decreaseQuantity);
        btnIncreaseQuantity = findViewById(R.id.btn_increaseQuantity);
        btnIngredientAdd = findViewById(R.id.btn_ingredient_add);
        tvItemName = findViewById(R.id.tv_itemName);
        quantityText = findViewById(R.id.quantityText);
        ivItemImage = findViewById(R.id.iv_itemImage);
        etExpirationDate = findViewById(R.id.et_expirationDate);

        // 초기 수량 설정
        quantityText.setText(String.valueOf(quantity));

        // Intent에서 데이터 받기
        Intent intent = getIntent();
        if (intent != null) {
            String itemName = intent.getStringExtra("itemName");
            int itemImage = intent.getIntExtra("itemImage", 0);

            tvItemName.setText(itemName);
            ivItemImage.setImageResource(itemImage);
        }

        // 수량 감소 버튼
        btnDecreaseQuantity.setOnClickListener(v -> {
            if (quantity > 0) {
                quantity--;
                quantityText.setText(String.valueOf(quantity));
            } else {
                Toast.makeText(this, "수량은 0보다 작을 수 없습니다", Toast.LENGTH_SHORT).show();
            }
        });

        // 수량 증가 버튼
        btnIncreaseQuantity.setOnClickListener(v -> {
            quantity++;
            quantityText.setText(String.valueOf(quantity));
        });

        // 유통기한 입력을 위한 날짜 선택기
        etExpirationDate.setOnClickListener(v -> showDatePickerDialog());

        // 재료 추가 버튼
        btnIngredientAdd.setOnClickListener(v -> {
            String ingredientName = tvItemName.getText().toString();
            String expirationDate = etExpirationDate.getText().toString();
            ApiRequest.addIngredient(this, ingredientName, quantity, expirationDate);
        });

        // 뒤로가기 버튼
        btnBack.setOnClickListener(v -> finish());
    }

    private void showDatePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    String date = selectedYear + "-" + (selectedMonth + 1) + "-" + selectedDay;
                    etExpirationDate.setText(date);
                }, year, month, day);
        datePickerDialog.show();
    }
}
