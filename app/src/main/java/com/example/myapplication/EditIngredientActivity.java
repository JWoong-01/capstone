package com.example.myapplication;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.VolleyError;

public class EditIngredientActivity extends AppCompatActivity {

    private Button btnBack, btnDecreaseQuantity, btnIncreaseQuantity, btnSave;
    private TextView tvItemName, quantityText;
    private ImageView ivItemImage;
    private EditText etExpirationDate;
    private int quantity = 0;
    private Ingredient ingredient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_ingredient);

        // 뷰 초기화
        btnBack = findViewById(R.id.btn_back);
        btnDecreaseQuantity = findViewById(R.id.btn_decreaseQuantity);
        btnIncreaseQuantity = findViewById(R.id.btn_increaseQuantity);
        btnSave = findViewById(R.id.btn_save);  // 이 버튼을 저장 버튼으로 사용
        tvItemName = findViewById(R.id.edt_name);
        quantityText = findViewById(R.id.edt_quantity);
        ivItemImage = findViewById(R.id.iv_itemImage);
        etExpirationDate = findViewById(R.id.edt_expiration_date);

        // Intent에서 데이터 받기
        Intent intent = getIntent();
        if (intent != null) {
            // 재료 객체 받아오기
            ingredient = (Ingredient) intent.getSerializableExtra("ingredient");
            if (ingredient != null) {
                // 받은 데이터를 뷰에 설정
                tvItemName.setText(ingredient.getName());
                quantity = ingredient.getQuantity();
                quantityText.setText(String.valueOf(quantity));
                ivItemImage.setImageResource(ingredient.getImageResId());
                etExpirationDate.setText(ingredient.getExpirationDate());
            }
        }

        // 뒤로가기 버튼
        btnBack.setOnClickListener(v -> finish());

        // 수량 감소 버튼
        btnDecreaseQuantity.setOnClickListener(v -> {
            if (quantity > 0) { // 수량이 0보다 큰 경우에만 감소
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

        // 수정된 재료 저장 버튼
        btnSave.setOnClickListener(v -> {
            if (quantity == 0) { // 수량이 0인 경우 경고 메시지를 표시하고 리턴
                Toast.makeText(this, "수량이 0일 경우 재료를 추가할 수 없습니다.", Toast.LENGTH_SHORT).show();
                return;
            }

            String ingredientName = tvItemName.getText().toString();
            String expirationDate = etExpirationDate.getText().toString();
            if (expirationDate.isEmpty()) { // 유통기한이 비어 있는 경우
                Toast.makeText(this, "유통기한을 입력해주세요.", Toast.LENGTH_SHORT).show();
                return;
            }

            // 이미지 리소스는 변경하지 않음, 수정이 필요하면 아래 코드에서 수정 가능
            int image = ingredient.getImageResId();
            String intakeDate = ingredient.getIntakeDate();  // 기존 입고 날짜 그대로 사용

            // ApiRequest 클래스 사용하여 서버에 재료 수정
            ApiRequest apiRequest = new ApiRequest(this);
            apiRequest.updateIngredient(ingredientName, quantity, intakeDate, expirationDate, image, new ApiRequest.ApiUpdateListener() {
                @Override
                public void onUpdateSuccess() {
                    Toast.makeText(EditIngredientActivity.this, "수정 완료!", Toast.LENGTH_SHORT).show();
                    finish();  // 수정 후 HomeActivity로 돌아가기
                }

                @Override
                public void onUpdateError() {
                    Toast.makeText(EditIngredientActivity.this, "수정 실패: ", Toast.LENGTH_SHORT).show();

                }
            });
        });
    }

    // 유통기한을 위한 날짜 선택기 다이얼로그 표시
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
