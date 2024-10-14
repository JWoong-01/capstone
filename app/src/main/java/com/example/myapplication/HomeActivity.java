package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;;
import android.widget.Button;

public class HomeActivity extends AppCompatActivity {
    private Button btn_edit, btn_delete, btn_add_freezer,
            btn_add_fredge, btn_fredge, btn_recipe, btn_setting;
    private TextView tv_Title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        tv_Title = findViewById(R.id.tv_title);
        btn_edit = findViewById(R.id.btn_edit);
        btn_delete = findViewById(R.id.btn_delete);
        btn_add_freezer = findViewById(R.id.btn_add_freezer);
        btn_add_fredge = findViewById(R.id.btn_add_fredge);
        btn_fredge = findViewById(R.id.btn_fredge);
        btn_recipe = findViewById(R.id.btn_recipe);
        btn_setting = findViewById(R.id.btn_setting);

        Intent intent = getIntent();

        btn_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        btn_add_freezer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        btn_add_fredge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        btn_fredge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        btn_recipe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        btn_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
    }
}