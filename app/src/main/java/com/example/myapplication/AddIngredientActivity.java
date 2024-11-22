package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class AddIngredientActivity extends AppCompatActivity {
    private Button btn_fredge;
    private EditText searchBar;  // EditText 객체


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_ingredient);

        TabLayout tabLayout = findViewById(R.id.category_tab);
        ViewPager2 viewPager = findViewById(R.id.ingredient_view);

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
                        case 3:
                            tab.setText("고기");
                            break;
                        case 4:
                            tab.setText("수산물");
                            break;
                        case 5:
                            tab.setText("양념");
                            break;
                        default:
                            tab.setText("기타");
                            break;
                    }
                }).attach();

        // btn_fredge 버튼 초기화
        btn_fredge = findViewById(R.id.btn_fredge);

        // btn_fredge 클릭 리스너 설정
        btn_fredge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddIngredientActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });

        // 검색바 처리
        searchBar = findViewById(R.id.search_bar);
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int before, int count) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                // 검색어가 변경될 때마다 어댑터에 전달
                adapter.setSearchQuery(charSequence.toString());

            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }
}