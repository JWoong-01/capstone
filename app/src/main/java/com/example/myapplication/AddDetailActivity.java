package com.example.myapplication;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import androidx.appcompat.app.AppCompatActivity;

public class AddDetailActivity extends AppCompatActivity {

    private Button btn_fredge, btnSetting,btnRecipe,btnBack, btnDecreaseQuantity, btnIncreaseQuantity, btnIngredientAdd;
    private TextView tvItemName, quantityText;
    private ImageView ivItemImage;
    private EditText etExpirationDate;
    private int quantity = 0;
    private RadioGroup rgStorage; // 저장 장소 선택 RadioGroup


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
        rgStorage = findViewById(R.id.rg_storage); // RadioGroup 초기화


        // 초기 수량 설정
        quantityText.setText(String.valueOf(quantity));

        btnSetting = findViewById(R.id.btn_setting);
        btnRecipe = findViewById(R.id.btn_recipe);
        btn_fredge = findViewById(R.id.btn_fredge);

        btn_fredge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddDetailActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });

        btnSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddDetailActivity.this, SettingActivity.class);
                startActivity(intent);
            }
        });

        btnRecipe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddDetailActivity.this, RecipeActivity.class);
                startActivity(intent);
            }
        });



        // Intent에서 데이터 받기
        Intent intent = getIntent();
        if (intent != null) {
            String itemName = intent.getStringExtra("itemName"); // 재료 이름 받기
            int itemImage = intent.getIntExtra("itemImage", 0); // 재료 이미지 리소스 ID 받기

            // 받은 데이터를 뷰에 설정
            tvItemName.setText(itemName); // 재료 이름 설정
            ivItemImage.setImageResource(itemImage); // 재료 이미지 설정
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

        // 재료 추가 버튼
        btnIngredientAdd.setOnClickListener(v -> {
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

            int image  = getIntent().getIntExtra("itemImage", R.drawable.ic_trashcan); // 기본값은 trashcan 이미지
            ivItemImage.setImageResource(image);

            // 저장 장소 선택
            int selectedStorageId = rgStorage.getCheckedRadioButtonId(); // 선택된 저장 장소의 ID 가져오기
            RadioButton selectedStorage = findViewById(selectedStorageId);
            String storageLocation = selectedStorage != null ? selectedStorage.getText().toString() : "냉장"; // 기본값은 냉장

            // 현재 날짜를 입고 날짜로 설정
            String intakeDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

            // ApiRequest 클래스 사용하여 서버에 재료 추가
            ApiRequest apiRequest = new ApiRequest(this);
            apiRequest.addIngredient(ingredientName, quantity, intakeDate, expirationDate, storageLocation, image );

            // 알림 설정
            scheduleNotification(ingredientName, expirationDate);

            Intent returnIntent = new Intent(AddDetailActivity.this, AddIngredientActivity.class);
            startActivity(returnIntent);
            finish();
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

    private void scheduleNotification(String ingredientName, String expirationDate) {
        // 날짜 형식 파싱 및 알림 예약
        Calendar calendar = Calendar.getInstance();
        // 예: expirationDate는 "2023-12-31" 형식으로 제공된다고 가정
        String[] dateParts = expirationDate.split("-");
        if (dateParts.length == 3) {
            int year = Integer.parseInt(dateParts[0]);
            int month = Integer.parseInt(dateParts[1]) - 1; // Calendar는 0-based month
            int day = Integer.parseInt(dateParts[2]);
            calendar.set(year, month, day, 9, 0); // 알림 시간: 오전 9시

            // 현재 시간보다 과거 날짜인지 확인
            if (calendar.getTimeInMillis() > System.currentTimeMillis()) {
                Intent notificationIntent = new Intent(this, NotificationReceiver.class);
                notificationIntent.putExtra("ingredientName", ingredientName);

                PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
                AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);

                Toast.makeText(this, "알림이 설정되었습니다.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "유통기한이 이미 지난 날짜입니다.", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
