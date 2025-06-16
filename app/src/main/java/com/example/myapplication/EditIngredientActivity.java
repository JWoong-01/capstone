package com.example.myapplication;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
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
    private EditText etExpirationDate;
    private Spinner spinnerUnit;
    private RadioGroup rgStorage;
    private RadioButton rbFridge, rbFreezer;

    private int quantity = 0;
    private Ingredient ingredient;

    @SuppressLint("WrongViewCast")
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
        spinnerUnit = findViewById(R.id.spinner_unit);
        etExpirationDate = findViewById(R.id.et_expirationDate);
        rgStorage = findViewById(R.id.rg_storage);
        rbFridge = findViewById(R.id.rb_fridge);
        rbFreezer = findViewById(R.id.rb_freezer);

        // 유통기한 EditText 설정 - 직접 입력 방지
        etExpirationDate.setInputType(InputType.TYPE_NULL);
        etExpirationDate.setFocusable(false);

        // 단위 스피너 설정
        ArrayAdapter<CharSequence> unitAdapter = ArrayAdapter.createFromResource(
                this,
                R.array.unit_array, // strings.xml에 정의된 배열
                android.R.layout.simple_spinner_item
        );
        unitAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerUnit.setAdapter(unitAdapter);

        // 기존 재료의 단위를 spinner에서 선택
        if (ingredient != null) {
            String unit = ingredient.getUnit();
            if (unit != null) {
                int spinnerPosition = unitAdapter.getPosition(unit);
                spinnerUnit.setSelection(spinnerPosition);
            }
        }

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
                String matchedName = IngredientData.getMatchedKoreanName(ingredient.getName());
                int imageResId = IngredientData.getImageResource(matchedName);
                ivItemImage.setImageResource(imageResId);
                ingredient.setImageResId(imageResId);
                String unit = ingredient.getUnit();
                if (unit != null) {
                    ArrayAdapter<CharSequence> adapter = (ArrayAdapter<CharSequence>) spinnerUnit.getAdapter();
                    int spinnerPosition = adapter.getPosition(unit);
                    spinnerUnit.setSelection(spinnerPosition);
                }
                // 날짜 설정
                Calendar calendar = ingredient.getExpirationDate();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                etExpirationDate.setText(sdf.format(calendar.getTime()));

                // 저장 장소 선택
                if ("냉장".equals(ingredient.getStorageLocation())) {
                    rbFridge.setChecked(true);
                } else if ("냉동".equals(ingredient.getStorageLocation())) {
                    rbFreezer.setChecked(true);
                }
            }
        }

        // 수량 감소ㅂㅈ
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
            String unit = spinnerUnit.getSelectedItem().toString(); // EditText에서 Spinner로 변경됨
            String dateStr = etExpirationDate.getText().toString().trim();
            String storage = rbFridge.isChecked() ? "냉장" : rbFreezer.isChecked() ? "냉동" : "";

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

            Calendar expirationCalendar = Calendar.getInstance();
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                expirationCalendar.setTime(sdf.parse(dateStr));
            } catch (ParseException e) {
                Toast.makeText(this, "유통기한 형식이 잘못되었습니다.", Toast.LENGTH_SHORT).show();
                return;
            }

            // 서버에 수정 요청 보내기
            ApiRequest apiRequest = new ApiRequest(this);
            apiRequest.updateIngredient(
                    name,
                    quantity,
                    unit,
                    ingredient.getIntakeDate(), // 기존 섭취일은 그대로 유지한다고 가정
                    dateStr,
                    storage,
                    ingredient.getImageResId(),
                    new ApiRequest.ApiUpdateListener() {
                        @Override
                        public void onUpdateSuccess() {
                            Toast.makeText(EditIngredientActivity.this, "재료가 성공적으로 수정되었습니다.", Toast.LENGTH_SHORT).show();

                            // 수정된 객체 로컬에도 반영
                            ingredient.setQuantity(quantity);
                            ingredient.setUnit(unit);
                            ingredient.setExpirationDate(expirationCalendar);
                            ingredient.setStorageLocation(storage);

                            Intent resultIntent = new Intent();
                            resultIntent.putExtra("updateIngredient", ingredient);
                            setResult(RESULT_OK, resultIntent);
                            finish();
                        }

                        @Override
                        public void onUpdateError() {
                            Toast.makeText(EditIngredientActivity.this, "수정 실패. 서버를 확인해주세요.", Toast.LENGTH_SHORT).show();
                        }
                    }
            );
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