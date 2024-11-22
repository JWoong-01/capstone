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

    private static final String ARG_SEARCH_QUERY = "searchQuery";

    // 새로운 인스턴스를 생성하고 검색어를 전달
    public static FruitFragment newInstance(String searchQuery) {
        FruitFragment fragment = new FruitFragment();
        Bundle args = new Bundle();
        args.putString(ARG_SEARCH_QUERY, searchQuery);  // 검색어 전달
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        String searchQuery = getArguments().getString(ARG_SEARCH_QUERY, "");
        // 카테고리 Fragment 레이아웃을 반환
        return inflater.inflate(R.layout.add_fruit, container, false);
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
