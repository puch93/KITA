package kr.co.core.kita.fragment;


import android.content.Intent;
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
import kr.co.core.kita.activity.CallHistoryAct;
import kr.co.core.kita.activity.SearchAct;
import kr.co.core.kita.adapter.TalkPagerAdapter;
import kr.co.core.kita.databinding.FragmentTalkBinding;
import kr.co.core.kita.util.StringUtil;

import static android.app.Activity.RESULT_OK;

public class TalkFrag extends BaseFrag {
    private FragmentTalkBinding binding;
    private AppCompatActivity act;

    private static final int SEARCH = 101;
    private int currentPos = 0;
    private String search_result_01 = "";
    private String search_result_02 = "";
    private TalkPagerAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_talk, container, false);
        act = (AppCompatActivity) getActivity();

        setLayout();

        binding.flHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(act, CallHistoryAct.class));
            }
        });

        binding.flSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentPos == 0) {
                    startActivityForResult(new Intent(act, SearchAct.class).putExtra("data", search_result_01), SEARCH);
                } else {
                    startActivityForResult(new Intent(act, SearchAct.class).putExtra("data", search_result_02), SEARCH);
                }
            }
        });

        return binding.getRoot();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK) {
            switch (requestCode) {
                case SEARCH:
                    if(currentPos == 0) {
                        Log.i(StringUtil.TAG, "onActivityResult: false");
                        search_result_01 = data.getStringExtra("value");
                        TalkSubFrag frag = (TalkSubFrag) adapter.instantiateItem(binding.viewPager, 0);
                        frag.setSearch(search_result_01);
                    } else {
                        Log.i(StringUtil.TAG, "onActivityResult: true");
                        search_result_02 = data.getStringExtra("value");
                        TalkSubFrag frag = (TalkSubFrag) adapter.instantiateItem(binding.viewPager, 1);
                        frag.setSearch(search_result_02);
                    }
                    break;
            }
        }
    }

    private void setLayout() {
        // set view pager
        adapter = new TalkPagerAdapter(act.getSupportFragmentManager());
        binding.viewPager.setAdapter(adapter);
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
                currentPos = tab.getPosition();

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
