package kr.co.core.kita.adapter;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import kr.co.core.kita.fragment.BaseFrag;
import kr.co.core.kita.fragment.HomeLocationFrag;
import kr.co.core.kita.fragment.HomeNewestFrag;
import kr.co.core.kita.fragment.HomePopularFrag;

public class HomePagerAdapter extends FragmentStatePagerAdapter {


    public void setSearch_result_01(String search_result_01) {
        this.search_result_01 = search_result_01;
    }
    public void setSearch_result_02(String search_result_02) {
        this.search_result_02 = search_result_02;
    }

    private String search_result_01 = "";
    private String search_result_02 = "";

    public HomePagerAdapter(@NonNull FragmentManager fm) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        BaseFrag currentFragment = null;
        switch (position) {
            case 0:
                HomePopularFrag frag_popular = new HomePopularFrag();
                frag_popular.setSearchText(search_result_01);
                currentFragment = frag_popular;
                break;
            case 1:
                HomeNewestFrag frag_newest = new HomeNewestFrag();
                frag_newest.setSearchText(search_result_02);
                currentFragment = frag_newest;
                break;
            case 2:
                currentFragment = new HomeLocationFrag();
                break;
        }

        return currentFragment;
    }

    @Override
    public int getCount() {
        return 3;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        return (BaseFrag) super.instantiateItem(container, position);
    }
}
