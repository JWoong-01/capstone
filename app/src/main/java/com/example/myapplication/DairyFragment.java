package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class DairyFragment extends Fragment {

    public DairyFragment() {
        // 기본 생성자
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // 유제품 카테고리 Fragment 레이아웃을 반환
        return inflater.inflate(R.layout.add_dairy_products, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 버튼 ID 배열
        int[] buttonIds = {
                R.id.iv_milk, R.id.iv_yogurt, R.id.iv_cheese, R.id.iv_soymilk,
                R.id.iv_margarine, R.id.iv_butter, R.id.iv_almondmilk, R.id.iv_greekyogurt
        };

        // 재료 이름 배열
        String[] itemNames = {
                "우유", "요거트", "치즈", "두유", "마가린", "버터", "아몬드 우유", "그릭 요거트"
        };

        // 재료 이미지 리소스 배열
        int[] itemImages = {
                R.drawable.it_milk, R.drawable.it_yogurt, R.drawable.it_cheese, R.drawable.it_milk,
                R.drawable.it_margarine, R.drawable.it_butter, R.drawable.it_milk, R.drawable.it_yogurt
        };

        // 각 버튼에 클릭 리스너 추가
        for (int i = 0; i < buttonIds.length; i++) {
            final int index = i;  // 클릭 리스너 안에서 사용하기 위한 인덱스
            View button = view.findViewById(buttonIds[i]);

            // 버튼이 null인지 확인
            if (button == null) {
                Log.e("DairyFragment", "Button with ID " + buttonIds[i] + " not found!");
                continue;  // 버튼을 찾을 수 없으면 다음 버튼으로 넘어감
            }

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // AddDetailActivity로 이동
                    Intent intent = new Intent(getActivity(), AddDetailActivity.class);
                    intent.putExtra("itemName", itemNames[index]); // 재료 이름
                    intent.putExtra("itemImage", itemImages[index]); // 재료 이미지 리소스 ID
                    startActivity(intent);
                }
            });
        }
    }
}