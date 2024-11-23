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
            case 1:
                return new VegetableFragment();  // 채소 카테고리
            case 2:
                return new DairyFragment();  // 유제품 카테고리
            case 3:
                return new MeatFragment();  // 고기
            case 4:
                return new SeafoodFragment();  // 수산물
            case 5:
                return new SauceFragment();  // 양념
            case 6:
                return new OtherFragment(); //기타
            default:
                return new Fragment();  // 기본 Fragment 반환
        }
    }

    @Override
    public int getItemCount() {
        return 7;  // 탭 개수
    }
}