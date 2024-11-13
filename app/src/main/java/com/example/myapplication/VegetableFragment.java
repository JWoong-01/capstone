package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class VegetableFragment extends Fragment {

    public VegetableFragment() {
        // 기본 생성자
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // 카테고리 Fragment 레이아웃을 반환
        return inflater.inflate(R.layout.add_vegetable, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 버튼 ID 배열
        int[] buttonIds = {
                R.id.btn_add_1, R.id.btn_add_2, R.id.btn_add_3, R.id.btn_add_4,
                R.id.btn_add_5, R.id.btn_add_6, R.id.btn_add_7, R.id.btn_add_8,
                R.id.btn_add_9, R.id.btn_add_10, R.id.btn_add_11, R.id.btn_add_12
        };

        // 재료 이름 배열
        String[] itemNames = {
                "당근", "피망", "가지", "양파", "감자", "고구마", "브로콜리", "대파",
                "마늘", "애호박", "고추", "양배추"
        };

        // 재료 이미지 리소스 배열
        int[] itemImages = {
                R.drawable.it_carrot, R.drawable.it_paprika, R.drawable.it_eggplant,
                R.drawable.it_onion, R.drawable.it_potato, R.drawable.it_sweetpotato,
                R.drawable.it_broccoli, R.drawable.it_greenonion, R.drawable.it_garlic,
                R.drawable.it_zucchini, R.drawable.it_pepper, R.drawable.it_cabbage
        };

        // 각 버튼에 클릭 리스너 추가
        for (int i = 0; i < buttonIds.length; i++) {
            final int index = i;  // 클릭 리스너 안에서 사용하기 위한 인덱스
            View button = view.findViewById(buttonIds[i]);

            // 버튼이 null인지 확인
            if (button == null) {
                Log.e("VegetableFragment", "Button with ID " + buttonIds[i] + " not found!");
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
