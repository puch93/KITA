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

import com.google.android.material.tabs.TabLayout;

import kr.co.core.kita.R;
import kr.co.core.kita.databinding.FragmentHomeBinding;
import kr.co.core.kita.util.StringUtil;

public class HomeLocationFrag extends BaseFrag {
    private FragmentHomeBinding binding;
    private Activity act;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false);
        act = (AppCompatActivity) getActivity();

        setTabLayout();


        return binding.getRoot();
    }

    private void setViewPager() {

    }

    /**
     * tab 관련 코드
     * selected / unselected 타이틀 텍스트 -> BOLD / NORMAL
     */
    private void setTabLayout() {
        LinearLayout tabLayout = (LinearLayout) ((ViewGroup) binding.tabLayout.getChildAt(0)).getChildAt(0);
        TextView tabTextView = (TextView) tabLayout.getChildAt(1);
        tabTextView.setTypeface(null, Typeface.BOLD);

        binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Log.i(StringUtil.TAG, "onTabSelected: " + tab.getPosition());

                LinearLayout tabLayout = (LinearLayout) ((ViewGroup) binding.tabLayout.getChildAt(0)).getChildAt(tab.getPosition());
                TextView tabTextView = (TextView) tabLayout.getChildAt(1);
                tabTextView.setTypeface(null, Typeface.BOLD);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                Log.i(StringUtil.TAG, "onTabUnselected: " + tab.getPosition());

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
