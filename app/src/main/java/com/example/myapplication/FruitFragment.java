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

        // 버튼 ID 배열
        int[] buttonIds = {
                R.id.iv_apple, R.id.iv_strawberry, R.id.iv_orange, R.id.iv_peach,
                R.id.iv_grape, R.id.iv_pear, R.id.iv_banana, R.id.iv_cherry,
                R.id.iv_mango, R.id.iv_kiwi, R.id.iv_pineapple, R.id.iv_watermelon
        };

        // 재료 이름 배열
        String[] itemNames = {
                "사과", "딸기", "오렌지", "복숭아", "포도", "배", "바나나", "체리",
                "망고", "키위", "파인애플", "수박"
        };

        // 재료 이미지 리소스 배열
        int[] itemImages = {
                R.drawable.it_apple, R.drawable.it_strawberry, R.drawable.it_orange,
                R.drawable.it_peach, R.drawable.it_grape, R.drawable.it_pear,
                R.drawable.it_banana, R.drawable.it_cherry, R.drawable.it_mango,
                R.drawable.it_kiwi, R.drawable.it_pineapple, R.drawable.it_watermelon
        };

        // 각 버튼에 클릭 리스너 추가
        for (int i = 0; i < buttonIds.length; i++) {
            final int index = i;  // 클릭 리스너 안에서 사용하기 위한 인덱스
            View button = view.findViewById(buttonIds[i]);

            // 버튼이 null인지 확인
            if (button == null) {
                Log.e("FruitFragment", "Button with ID " + buttonIds[i] + " not found!");
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