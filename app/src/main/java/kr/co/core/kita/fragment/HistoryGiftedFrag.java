package kr.co.core.kita.fragment;


import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import kr.co.core.kita.R;
import kr.co.core.kita.adapter.HistoryGiftAdapter;
import kr.co.core.kita.data.HistoryGiftData;
import kr.co.core.kita.databinding.FragmentHistoryGiftedBinding;
import kr.co.core.kita.util.AllOfDecoration;

public class HistoryGiftedFrag extends BaseFrag {
    private FragmentHistoryGiftedBinding binding;
    private Activity act;


    private LinearLayoutManager manager;
    private HistoryGiftAdapter adapter;
    private ArrayList<HistoryGiftData> list = new ArrayList<>();
    private boolean isScroll = false;
    private int page = 1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_history_gifted, container, false);
        act = (AppCompatActivity) getActivity();

        // set recycler view
        manager = new LinearLayoutManager(act);
        binding.recyclerView.setLayoutManager(manager);
        binding.recyclerView.setItemViewCacheSize(20);
        binding.recyclerView.setHasFixedSize(true);

        adapter = new HistoryGiftAdapter(act, list);
        binding.recyclerView.setAdapter(adapter);
        binding.recyclerView.addItemDecoration(new AllOfDecoration(act, AllOfDecoration.HISTORY_GIFT_LIST));

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
            list.add(new HistoryGiftData(null, null,null, "테스터" + i, "JOMALONE S/S Cologne", "￦194,500"));
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
