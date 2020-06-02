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
import kr.co.core.kita.activity.PaymentAct;
import kr.co.core.kita.activity.SearchAct;
import kr.co.core.kita.adapter.HomePagerAdapter;
import kr.co.core.kita.databinding.FragmentHomeBinding;
import kr.co.core.kita.util.StringUtil;

import static android.app.Activity.RESULT_OK;

public class HomeFrag extends BaseFrag {
    private FragmentHomeBinding binding;
    private AppCompatActivity act;

    private static final int SEARCH = 101;
    private HomePagerAdapter adapter;
    private int currentPos = 0;
    private String search_result_01 = "";
    private String search_result_02 = "";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false);
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

        binding.flPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(act, PaymentAct.class));
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
                        search_result_01 = data.getStringExtra("value");
                        HomePopularFrag frag = (HomePopularFrag) adapter.instantiateItem(binding.viewPager, 0);
                        frag.setSearch(search_result_01);
                        adapter.setSearch_result_01(search_result_01);
                    } else {
                        search_result_02 = data.getStringExtra("value");
                        HomeNewestFrag frag = (HomeNewestFrag) adapter.instantiateItem(binding.viewPager, 1);
                        frag.setSearch(search_result_02);
                        adapter.setSearch_result_02(search_result_02);
                    }
                    break;
            }
        }
    }

    private void setLayout() {
        // set view pager
        adapter = new HomePagerAdapter(act.getSupportFragmentManager());
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

        // set tab layout
        LinearLayout tabLayout3 = (LinearLayout) ((ViewGroup) binding.tabLayout.getChildAt(0)).getChildAt(2);
        TextView tabTextView3 = (TextView) tabLayout3.getChildAt(1);
        tabTextView3.setTypeface(null, Typeface.NORMAL);

        binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Log.i(StringUtil.TAG, "onTabSelected: " + tab.getPosition());
                currentPos = tab.getPosition();

                binding.viewPager.setCurrentItem(tab.getPosition(), true);

                if(currentPos != 2) {
                    binding.flSearch.setVisibility(View.VISIBLE);
                } else {
                    binding.flSearch.setVisibility(View.GONE);
                }


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
