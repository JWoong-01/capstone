package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class CategoryPagerAdapter extends FragmentStateAdapter {

    public CategoryPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new FruitFragment();  // 과일 카테고리
            //다른 fragment 파일 생성하시고 주석해제해서 테스트해주세요
//           case 1:
//                return new VegetableFragment();  // 채소 카테고리
//            case 2:
//                return new DairyFragment();  // 유제품 카테고리
            // 다른 카테고리 추가
            default:
                return new Fragment();  // 기본 Fragment 반환
        }
    }

    @Override
    public int getItemCount() {
        return 8;  // 탭 개수 (예: 과일, 채소, 유제품 등)
    }
}