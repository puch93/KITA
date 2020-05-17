package kr.co.core.kita.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import kr.co.core.kita.R;
import kr.co.core.kita.adapter.ChattingListAdapter;
import kr.co.core.kita.data.ChattingLIstData;
import kr.co.core.kita.databinding.FragmentChatBinding;

public class ChatFrag extends BaseFrag {
    FragmentChatBinding binding;
    Activity act;

    LinearLayoutManager manager;
    ChattingListAdapter adapter;
    ArrayList<ChattingLIstData> list = new ArrayList<>();

    boolean isScroll = false;
    int page = 1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_chat, container, false);
        act = getActivity();

        manager = new LinearLayoutManager(act);
        binding.recyclerView.setLayoutManager(manager);
        binding.recyclerView.setHasFixedSize(true);
        binding.recyclerView.setItemViewCacheSize(20);
        adapter = new ChattingListAdapter(act, list);
        binding.recyclerView.setAdapter(adapter);

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
        for (int i = 0; i < 10; i++) {
            list.add(new ChattingLIstData(null, null, "KITA JTV online Omise" + i, null, "Welcome to KITA live You...", "2020.02.29", String.valueOf(i)));
        }

        act.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                isScroll = false;
                adapter.setList(list);
            }
        });
    }
}
