package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class FruitFragment extends Fragment {

    public FruitFragment() {
        // 기본 생성자
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // 카테고리 Fragment 레이아웃을 반환
        return inflater.inflate(R.layout.add_fruit, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        View btn_apple = view.findViewById(R.id.btn_add_1);
        View btn_strawberry = view.findViewById(R.id.btn_add_2);
        View btn_peach = view.findViewById(R.id.btn_add_3);
        View btn_orange = view.findViewById(R.id.btn_add_4);

        // 클릭 리스너 추가
        btn_apple.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // AddDetailActivity로 이동
                Intent intent = new Intent(getActivity(), AddDetailActivity.class);
                intent.putExtra("itemName", "사과"); // 재료 이름
                intent.putExtra("itemImage", R.drawable.it_apple); // 재료 이미지 리소스 ID
                startActivity(intent);
            }
        });

        btn_strawberry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // AddDetailActivity로 이동
                Intent intent = new Intent(getActivity(), AddDetailActivity.class);
                intent.putExtra("itemName", "딸기"); // 재료 이름
                intent.putExtra("itemImage", R.drawable.it_strawberry); // 재료 이미지 리소스 ID
                startActivity(intent);
            }
        });

        btn_orange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // AddDetailActivity로 이동
                Intent intent = new Intent(getActivity(), AddDetailActivity.class);
                intent.putExtra("itemName", "오렌지"); // 재료 이름
                intent.putExtra("itemImage", R.drawable.it_orange); // 재료 이미지 리소스 ID
                startActivity(intent);
            }
        });

        btn_peach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // AddDetailActivity로 이동
                Intent intent = new Intent(getActivity(), AddDetailActivity.class);
                intent.putExtra("itemName", "복숭아"); // 재료 이름
                intent.putExtra("itemImage", R.drawable.it_peach); // 재료 이미지 리소스 ID
                startActivity(intent);
            }
        });


    }
}
