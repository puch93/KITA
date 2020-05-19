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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import kr.co.core.kita.R;
import kr.co.core.kita.adapter.TalkListAdapter;
import kr.co.core.kita.data.HomeMemberData;
import kr.co.core.kita.data.TalkListData;
import kr.co.core.kita.databinding.FragmentTalkLiveBinding;
import kr.co.core.kita.util.AllOfDecoration;
import kr.co.core.kita.util.StringUtil;

public class TalkLiveFrag extends BaseFrag {
    private FragmentTalkLiveBinding binding;
    private Activity act;


    private LinearLayoutManager manager;
    private TalkListAdapter adapter;
    private ArrayList<TalkListData> list = new ArrayList<>();
    private boolean isScroll = false;
    private int page = 1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_talk_live, container, false);
        act = (AppCompatActivity) getActivity();

        // set recycler view
        manager = new LinearLayoutManager(act);
        binding.recyclerView.setLayoutManager(manager);
        binding.recyclerView.setItemViewCacheSize(20);
        binding.recyclerView.setHasFixedSize(true);

        adapter = new TalkListAdapter(act, list);
        binding.recyclerView.setAdapter(adapter);
        binding.recyclerView.addItemDecoration(new AllOfDecoration(act, AllOfDecoration.TALK_LIST));

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

    public void setSearch(String contents) {

        if(!StringUtil.isNull(contents)) {
            isScroll = true;
            ArrayList<TalkListData> search_list = new ArrayList<>();
            for (int i = 0; i < list.size(); i++) {
                if(list.get(i).getContents().contains(contents)) {
                    search_list.add(list.get(i));
                }
            }

            adapter.setList(search_list);
        } else {
            adapter.setList(list);
            isScroll = false;
        }
    }

    private void setTestData() {
        isScroll = true;
        for (int i = 0; i < 10; i++) {
            list.add(new TalkListData(null, null,"테스터" + i, "테스터입니다" + i, "2020.02.09", null));
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
