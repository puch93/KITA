package kr.co.core.kita.fragment;


import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.tabs.TabLayout;

import kr.co.core.kita.R;
import kr.co.core.kita.adapter.HomeAdapter;
import kr.co.core.kita.adapter.HomePagerAdapter;
import kr.co.core.kita.databinding.FragmentHomeBinding;
import kr.co.core.kita.util.StringUtil;

public class HomeFrag extends BaseFrag {
    private FragmentHomeBinding binding;
    private AppCompatActivity act;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false);
        System.out.println("check spot 1");
        act = (AppCompatActivity) getActivity();

        setLayout();


        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        System.out.println("check spot 2");
    }

    private void setLayout() {
        // set view pager
        binding.viewPager.setAdapter(new HomePagerAdapter(act.getSupportFragmentManager()));
        binding.viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(binding.tabLayout));

        // set tab layout
        LinearLayout tabLayout = (LinearLayout) ((ViewGroup) binding.tabLayout.getChildAt(0)).getChildAt(0);
        TextView tabTextView = (TextView) tabLayout.getChildAt(1);
        tabTextView.setTypeface(null, Typeface.BOLD);

        // set tab layout
        LinearLayout tabLayout2 = (LinearLayout) ((ViewGroup) binding.tabLayout.getChildAt(0)).getChildAt(1);
        TextView tabTextView2 = (TextView) tabLayout2.getChildAt(1);
        tabTextView2.setTypeface(null, Typeface.NORMAL);

        binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Log.i(StringUtil.TAG, "onTabSelected: " + tab.getPosition());

                binding.viewPager.setCurrentItem(tab.getPosition(), true);


                // 선택된 탭 텍스트 BOLD 처리
                LinearLayout tabLayout = (LinearLayout) ((ViewGroup) binding.tabLayout.getChildAt(0)).getChildAt(tab.getPosition());
                TextView tabTextView = (TextView) tabLayout.getChildAt(1);
                tabTextView.setTypeface(null, Typeface.BOLD);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                Log.i(StringUtil.TAG, "onTabUnselected: " + tab.getPosition());

                // 선택된 탭 텍스트 NORMAL 처리
                LinearLayout tabLayout = (LinearLayout) ((ViewGroup) binding.tabLayout.getChildAt(0)).getChildAt(tab.getPosition());
                TextView tabTextView = (TextView) tabLayout.getChildAt(1);
                tabTextView.setTypeface(null, Typeface.NORMAL);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

}
