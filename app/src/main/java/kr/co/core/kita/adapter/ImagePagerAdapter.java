package kr.co.core.kita.adapter;


import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import java.util.ArrayList;

import kr.co.core.kita.fragment.ImageFrag;

public class ImagePagerAdapter extends FragmentStatePagerAdapter {
    ArrayList<String> list = new ArrayList<>();

    public ImagePagerAdapter(FragmentManager fm, ArrayList<String> list) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        this.list = list;
    }

    @Override
    public Fragment getItem(int i) {
        ImageFrag currentFragment = new ImageFrag();
        currentFragment.setData(list.get(i));
        return currentFragment;
    }

    public void setList(ArrayList<String> list) {
            this.list = list;
            notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        return super.instantiateItem(container, position);
    }
}