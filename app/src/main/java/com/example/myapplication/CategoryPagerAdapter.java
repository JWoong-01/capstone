package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class CategoryPagerAdapter extends FragmentStateAdapter {

    private String searchQuery = "";  // 검색어 저장용 변수

    public CategoryPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    // 검색어를 프래그먼트에 전달하기 위한 setter 메서드
    public void setSearchQuery(String query) {
        this.searchQuery = query;
        // 검색어가 변경될 때마다 각 프래그먼트를 갱신해야 하므로 notifyDataSetChanged 호출 가능
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        // 검색어를 필터로 적용하여 각 프래그먼트에 데이터를 전달
        switch (position) {
            case 0:
                return FruitFragment.newInstance(searchQuery);  // 과일 카테고리
            case 1:
                return VegetableFragment.newInstance(searchQuery);  // 채소 카테고리
            case 3:
                return MeatFragment.newInstance(searchQuery);  // 고기
            case 4:
                return SeafoodFragment.newInstance(searchQuery);  // 수산물
            case 5:
                return SauceFragment.newInstance(searchQuery);  // 양념
            default:
                return new Fragment();  // 기본 Fragment 반환
        }
    }

    @Override
    public int getItemCount() {
        return 6;  // 탭 개수 (현재 6개 탭)
    }
}
