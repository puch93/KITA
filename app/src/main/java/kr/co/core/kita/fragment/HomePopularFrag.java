package kr.co.core.kita.fragment;


import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import kr.co.core.kita.R;
import kr.co.core.kita.adapter.HomeMemberAdapter;
import kr.co.core.kita.data.HomeMemberData;
import kr.co.core.kita.databinding.FragmentHomePopularBinding;
import kr.co.core.kita.util.AllOfDecoration;
import kr.co.core.kita.util.StringUtil;

public class HomePopularFrag extends BaseFrag implements PopupMenu.OnMenuItemClickListener, View.OnClickListener {
    private FragmentHomePopularBinding binding;
    private Activity act;


    private GridLayoutManager manager;
    private HomeMemberAdapter adapter;
    private ArrayList<HomeMemberData> list = new ArrayList<>();
    private boolean isScroll = false;
    private int page = 1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home_popular, container, false);
        act = (AppCompatActivity) getActivity();

        // set click listener
        binding.tvSort.setOnClickListener(this);

        // set recycler view
        manager = new GridLayoutManager(act, 2);
        binding.recyclerView.setLayoutManager(manager);
        binding.recyclerView.setItemViewCacheSize(20);
        binding.recyclerView.setHasFixedSize(true);

        adapter = new HomeMemberAdapter(act, list);
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

    public void setSearch(String contents) {

        if(!StringUtil.isNull(contents)) {
            isScroll = true;
            ArrayList<HomeMemberData> search_list = new ArrayList<>();
            for (int i = 0; i < list.size(); i++) {
                if(list.get(i).getNick().contains(contents)) {
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

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        binding.tvSort.setText(item.getTitle());

        switch (item.getItemId()) {
            case R.id.sort_popularity:
                return true;
            case R.id.sort_heart:
                return true;

            default:
                return false;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_sort:
                PopupMenu popup = new PopupMenu(act, v);
                popup.setOnMenuItemClickListener(this);
                popup.inflate(R.menu.menu_home_popular);
                popup.show();
                break;
        }

    }
}
