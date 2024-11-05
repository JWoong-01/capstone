package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class AddIngredientActivity extends AppCompatActivity {
    private Button btn_fredge,btn_add_onion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_ingredient);

        TabLayout tabLayout = findViewById(R.id.category_tab);
        ViewPager2 viewPager = findViewById(R.id.view_pager);

        // 어댑터 설정
        CategoryPagerAdapter adapter = new CategoryPagerAdapter(this);
        viewPager.setAdapter(adapter);

        // TabLayout과 ViewPager2 연결
        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> {
                    switch (position) {
                        case 0:
                            tab.setText("과일");
                            break;
                        case 1:
                            tab.setText("채소");
                            break;
                        case 2:
                            tab.setText("유제품");
                            break;
                        // 다른 탭 추가
                    }
                }).attach();

        // btn_fredge 버튼 초기화
        btn_fredge = findViewById(R.id.btn_fredge);
        //ImageButton btn_add_onion = findViewById(R.id.btn_add_onion);

        // btn_fredge 클릭 리스너 설정
        btn_fredge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddIngredientActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });

        btn_add_onion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // AddDetailActivity로 이동
                Intent intent = new Intent(AddIngredientActivity.this, AddDetailActivity.class);
                intent.putExtra("itemName", "양파"); // 재료 이름
                intent.putExtra("itemImage", R.drawable.it_onion); // 재료 이미지 리소스 ID
                startActivity(intent);
            }
        });

    }
}