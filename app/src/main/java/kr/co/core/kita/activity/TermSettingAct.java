package kr.co.core.kita.activity;

import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.google.android.material.tabs.TabLayout;

import kr.co.core.kita.R;
import kr.co.core.kita.adapter.TermPagerAdapter;
import kr.co.core.kita.databinding.ActivityTermSettingBinding;
import kr.co.core.kita.util.StringUtil;

public class TermSettingAct extends BaseAct {
    ActivityTermSettingBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_term_setting);

        setLayout();

        binding.flBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void setLayout() {
        // set view pager
        binding.viewPager.setAdapter(new TermPagerAdapter(getSupportFragmentManager()));
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
