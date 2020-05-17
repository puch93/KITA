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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

import kr.co.core.kita.R;
import kr.co.core.kita.adapter.HomeAdapter;
import kr.co.core.kita.data.HomeMemberData;
import kr.co.core.kita.databinding.FragmentHomeBinding;
import kr.co.core.kita.databinding.FragmentHomePopularBinding;
import kr.co.core.kita.util.AllOfDecoration;
import kr.co.core.kita.util.StringUtil;

public class HomePopularFrag extends BaseFrag {
    private FragmentHomePopularBinding binding;
    private Activity act;


    private GridLayoutManager manager;
    private HomeAdapter adapter;
    private ArrayList<HomeMemberData> list = new ArrayList<>();
    private boolean isScroll = false;
    private int page = 1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home_popular, container, false);
        act = (AppCompatActivity) getActivity();

        // set click listener
        binding.tvSort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        // set recycler view
        manager = new GridLayoutManager(act, 2);
        binding.recyclerView.setLayoutManager(manager);
        binding.recyclerView.setItemViewCacheSize(20);
        binding.recyclerView.setHasFixedSize(true);

        adapter = new HomeAdapter(act, list);
        binding.recyclerView.setAdapter(adapter);
        binding.recyclerView.addItemDecoration(new AllOfDecoration(act, AllOfDecoration.HOME_LIST));

        binding.recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int totalCount = manager.getItemCount();
                int lastItemPosition = manager.findLastCompletelyVisibleItemPosition();
                if (!isScroll) {
                    if (totalCount - 1 == lastItemPosition) {
                        isScroll = true;
                        ++page;
//                        getList(page);
                        setTestData();
                    }
                }
            }
        });

        setTestData();

        return binding.getRoot();
    }

    private void setTestData() {
        isScroll = true;
        for (int i = 0; i < 10; i++) {
            list.add(new HomeMemberData(null, "테스터" + i, "테스터입니다" + i, null, i % 2 == 0));
        }
        isScroll = false;

        act.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.setList(list);
            }
        });
    }
}
