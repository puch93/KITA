package kr.co.core.kita.adapter;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import kr.co.core.kita.fragment.BaseFrag;
import kr.co.core.kita.fragment.HomeFrag;
import kr.co.core.kita.fragment.HomePopularFrag;

public class HomePagerAdapter extends FragmentStatePagerAdapter {

    public HomePagerAdapter(@NonNull FragmentManager fm) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        BaseFrag currentFragment = null;
        switch (position) {
            case 0:
            case 1:
                currentFragment = new HomePopularFrag();
                break;

        }

        return currentFragment;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        return (BaseFrag) super.instantiateItem(container, position);
    }
}
