package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

public class SauceFragment extends Fragment {

    public SauceFragment() {
        // 기본 생성자
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // add_sauce.xml 레이아웃을 Fragment로 반환
        return inflater.inflate(R.layout.add_sauce, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 버튼 ID 배열 (소스 추가 버튼)
        int[] buttonIds = {
                R.id.iv_ketchup, R.id.iv_mayonnaise, R.id.iv_mustard, R.id.iv_bbq,
                R.id.iv_hot, R.id.iv_soysauce, R.id.iv_sesameoil, R.id.iv_salt,
                R.id.iv_sugar, R.id.iv_gochujang, R.id.iv_oyster, R.id.iv_redpepperpowder,
                R.id.iv_doenjang, R.id.iv_ssamjang, R.id.iv_currypowder, R.id.iv_dressing,
                R.id.iv_fishsauce, R.id.iv_chicken_seasoning, R.id.iv_tomatopaste
        };

        // 재료 이름 배열 (소스 종류)
        String[] itemNames = {
                "케첩", "마요네즈", "머스타드", "바베큐 소스", "핫소스", "간장", "참기름", "소금",
                "설탕", "고추장", "굴소스", "고춧가루", "된장", "쌈장", "카레 가루", "샐러드 드레싱",
                "피시 소스", "치킨 스톡", "토마토 소스"
        };

        // 재료 이미지 리소스 배열 (소스 이미지)
        int[] itemImages = {
                R.drawable.it_ketchup, R.drawable.it_mayonnaise, R.drawable.it_mustard,R.drawable.it_bbq,
                R.drawable.it_hot, R.drawable.it_soysauce, R.drawable.it_sesameoil,R.drawable.it_salt,
                R.drawable.it_sugar, R.drawable.it_gochujang, R.drawable.it_oyster, R.drawable.it_redpepperpowder,
                R.drawable.it_paste, R.drawable.it_paste, R.drawable.it_currypowder, R.drawable.it_dressing,
                R.drawable.it_fishsauce, R.drawable.it_chicken_seasoning, R.drawable.it_tomatopaste
        };

        // 각 버튼에 클릭 리스너 추가
        for (int i = 0; i < buttonIds.length; i++) {
            final int index = i;  // 클릭 리스너 안에서 사용하기 위한 인덱스
            ImageButton button = view.findViewById(buttonIds[i]);

            // 버튼이 null인지 확인
            if (button == null) {
                Log.e("SauceFragment", "Button with ID " + buttonIds[i] + " not found!");
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