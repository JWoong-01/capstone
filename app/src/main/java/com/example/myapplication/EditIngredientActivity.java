package com.example.myapplication;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class EditIngredientActivity extends AppCompatActivity {

    private Button btnBack, btnDecreaseQuantity, btnIncreaseQuantity, btnSave, btnFredge;
    private TextView tvItemName, quantityText;
    private ImageView ivItemImage;
    private EditText etUnit, etExpirationDate;
    private RadioGroup rgStorage;
    private RadioButton rbFridge, rbFreezer;

    private int quantity = 0;
    private Ingredient ingredient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_ingredient);

        // View 초기화
        btnBack = findViewById(R.id.btn_back);
        btnDecreaseQuantity = findViewById(R.id.btn_decreaseQuantity);
        btnIncreaseQuantity = findViewById(R.id.btn_increaseQuantity);
        btnSave = findViewById(R.id.btn_save);
        btnFredge = findViewById(R.id.btn_fredge);
        tvItemName = findViewById(R.id.edt_name);
        quantityText = findViewById(R.id.edt_quantity);
        ivItemImage = findViewById(R.id.iv_itemImage);
        etUnit = findViewById(R.id.et_unit);
        etExpirationDate = findViewById(R.id.et_expirationDate);
        rgStorage = findViewById(R.id.rg_storage);
        rbFridge = findViewById(R.id.rb_fridge);
        rbFreezer = findViewById(R.id.rb_freezer);

        // 냉장고 버튼 -> 홈으로 이동
        btnFredge.setOnClickListener(v -> {
            Intent intent = new Intent(EditIngredientActivity.this, HomeActivity.class);
            startActivity(intent);
        });

        // 뒤로가기 버튼
        btnBack.setOnClickListener(v -> finish());

        // 데이터 받아오기
        Intent intent = getIntent();
        if (intent != null) {
            ingredient = (Ingredient) intent.getSerializableExtra("ingredient");
            if (ingredient != null) {
                tvItemName.setText(ingredient.getName());
                quantity = ingredient.getQuantity();
                quantityText.setText(String.valueOf(quantity));
                ivItemImage.setImageResource(ingredient.getImageResId());
                etUnit.setText(ingredient.getUnit());

                // 날짜 설정
                Calendar calendar = ingredient.getExpirationDate();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                etExpirationDate.setText(sdf.format(calendar.getTime()));

                // 저장 장소 선택
                if ("냉장실".equals(ingredient.getStorageLocation())) {
                    rbFridge.setChecked(true);
                } else if ("냉동실".equals(ingredient.getStorageLocation())) {
                    rbFreezer.setChecked(true);
                }
            }
        }

        // 수량 감소
        btnDecreaseQuantity.setOnClickListener(v -> {
            if (quantity > 0) {
                quantity--;
                quantityText.setText(String.valueOf(quantity));
            } else {
                Toast.makeText(this, "수량은 0보다 작을 수 없습니다.", Toast.LENGTH_SHORT).show();
            }
        });

        // 수량 증가
        btnIncreaseQuantity.setOnClickListener(v -> {
            quantity++;
            quantityText.setText(String.valueOf(quantity));
        });

        // 날짜 선택
        etExpirationDate.setOnClickListener(v -> showDatePickerDialog());

        // 저장 버튼
        btnSave.setOnClickListener(v -> {
            String name = tvItemName.getText().toString().trim();
            String unit = etUnit.getText().toString().trim();
            String dateStr = etExpirationDate.getText().toString().trim();
            String storage = rbFridge.isChecked() ? "냉장실" : rbFreezer.isChecked() ? "냉동실" : "";

            if (quantity == 0) {
                Toast.makeText(this, "수량이 0일 경우 재료를 추가할 수 없습니다.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (dateStr.isEmpty()) {
                Toast.makeText(this, "유통기한을 입력해주세요.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (storage.isEmpty()) {
                Toast.makeText(this, "저장 장소를 선택해주세요.", Toast.LENGTH_SHORT).show();
                return;
            }

            // 유통기한 Calendar로 변환
            Calendar expirationCalendar = Calendar.getInstance();
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                expirationCalendar.setTime(sdf.parse(dateStr));
            } catch (ParseException e) {
                Toast.makeText(this, "유통기한 형식이 잘못되었습니다.", Toast.LENGTH_SHORT).show();
                return;
            }

            // ingredient 객체 업데이트
            ingredient.setQuantity(quantity);
            ingredient.setUnit(unit);
            ingredient.setExpirationDate(expirationCalendar);
            ingredient.setStorageLocation(storage);

            // 결과 전달
            Intent resultIntent = new Intent();
            resultIntent.putExtra("updatedIngredient", ingredient);
            setResult(RESULT_OK, resultIntent);
            finish();
        });
    }

    private void showDatePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                EditIngredientActivity.this,
                (view, year, month, dayOfMonth) -> {
                    calendar.set(year, month, dayOfMonth);
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    etExpirationDate.setText(sdf.format(calendar.getTime()));
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }
}
