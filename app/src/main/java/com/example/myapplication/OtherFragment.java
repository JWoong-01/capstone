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

public class OtherFragment extends Fragment {
    public OtherFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // 카테고리 Fragment 레이아웃을 반환
        return inflater.inflate(R.layout.add_other, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 버튼 ID 배열
        int[] buttonIds = {
                R.id.iv_egg, R.id.iv_fishcake, R.id.iv_ricecake, R.id.iv_ricecake2,
                R.id.iv_dumpling, R.id.iv_cutlet, R.id.iv_bread, R.id.iv_spaghetti,
                R.id.iv_noodle, R.id.iv_kimchi
        };

        // 재료 이름 배열
        String[] itemNames = {
                "계란", "어묵", "떡볶이 떡", "떡국떡", "만두", "돈까스", "식빵", "스파게티 면",
                "소면", "김치"
        };

        // 재료 이미지 리소스 배열
        int[] itemImages = {
                R.drawable.it_egg, R.drawable.it_fishcake, R.drawable.it_ricecake, R.drawable.it_ricecake
                , R.drawable.it_dumpling, R.drawable.it_cutlet, R.drawable.it_bread, R.drawable.it_noodle,
                R.drawable.it_noodle, R.drawable.it_kimchi
        };

        // 각 버튼에 클릭 리스너 추가
        for (int i = 0; i < buttonIds.length; i++) {
            final int index = i;  // 클릭 리스너 안에서 사용하기 위한 인덱스
            View button = view.findViewById(buttonIds[i]);

            // 버튼이 null인지 확인
            if (button == null) {
                Log.e("MeatFragment", "Button with ID " + buttonIds[i] + " not found!");
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