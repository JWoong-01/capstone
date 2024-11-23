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

public class MeatFragment extends Fragment {

    public MeatFragment() {
        // 기본 생성자
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // 카테고리 Fragment 레이아웃을 반환
        return inflater.inflate(R.layout.add_meat, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 버튼 ID 배열
        int[] buttonIds = {
                R.id.iv_beef, R.id.iv_pork, R.id.iv_chicken, R.id.iv_lamb, R.id.iv_ham, R.id.iv_sausage,
                R.id.iv_duck, R.id.iv_marinatedmeat, R.id.iv_drumsticks, R.id.iv_breast, R.id.iv_bacon
        };

        // 재료 이름 배열
        String[] itemNames = {
                "소고기", "돼지고기", "닭고기", "양고기", "햄", "소시지",
                "오리고기", "양념고기", "닭다리", "닭가슴살", "베이컨"
        };

        // 재료 이미지 리소스 배열
        int[] itemImages = {
                R.drawable.it_beef, R.drawable.it_pork, R.drawable.it_chicken,R.drawable.it_lamb
                , R.drawable.it_ham, R.drawable.it_sausage,R.drawable.it_duck, R.drawable.it_marinatedmeat,
                R.drawable.it_drumsticks, R.drawable.it_breast, R.drawable.it_bacon
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