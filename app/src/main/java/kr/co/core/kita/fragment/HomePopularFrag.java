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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import kr.co.core.kita.R;
import kr.co.core.kita.adapter.HomeMemberAdapter;
import kr.co.core.kita.data.HomeMemberData;
import kr.co.core.kita.databinding.FragmentHomePopularBinding;
import kr.co.core.kita.server.ReqBasic;
import kr.co.core.kita.server.netUtil.HttpResult;
import kr.co.core.kita.server.netUtil.NetUrls;
import kr.co.core.kita.util.AllOfDecoration;
import kr.co.core.kita.util.AppPreference;
import kr.co.core.kita.util.Common;
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
                        ++page;
                        getHomePopularList();
                    }
                }
            }
        });

        getHomePopularList();

        return binding.getRoot();
    }


    private void getHomePopularList() {
        isScroll = true;
        ReqBasic server = new ReqBasic(act, NetUrls.LIST_HOME_POPULAR) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {
                if (resultData.getResult() != null) {
                    try {
                        JSONObject jo = new JSONObject(resultData.getResult());

                        if (StringUtil.getStr(jo, "result").equalsIgnoreCase("Y") || StringUtil.getStr(jo, "result").equalsIgnoreCase(NetUrls.SUCCESS)) {
                            JSONArray ja = jo.getJSONArray("data");
                            for (int i = 0; i < ja.length(); i++) {
                                JSONObject job = ja.getJSONObject(i);
                                String idx = StringUtil.getStr(job, "idx");
                                String loginYN = StringUtil.getStr(job, "loginYN");
                                String p_image1 = StringUtil.getStr(job, "p_image1");
                                String nick = StringUtil.getStr(job, "nick");
                                String intro = StringUtil.getStr(job, "intro");

                                list.add(new HomeMemberData(idx, nick, intro, p_image1, loginYN.equalsIgnoreCase("Y")));
                            }

                            act.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    adapter.setList(list);
                                }
                            });

                            isScroll = false;
                        } else {
                            if (page == 1)
                                Common.showToast(act, StringUtil.getStr(jo, "msg"));

                            isScroll = true;
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Common.showToastNetwork(act);

                        isScroll = false;
                    }
                } else {
                    Common.showToastNetwork(act);

                    isScroll = false;
                }
            }
        };

        server.setTag("Popular");
        server.addParams("m_idx", AppPreference.getProfilePref(act, AppPreference.PREF_MIDX));
        server.addParams("type", binding.tvSort.getText().toString().equalsIgnoreCase("Gift") ? "gift" : "heart");
        server.addParams("pagenum", String.valueOf(page));
        server.execute(true, false);
    }


    public void setSearch(String contents) {

        if (!StringUtil.isNull(contents)) {
            isScroll = true;
            ArrayList<HomeMemberData> search_list = new ArrayList<>();
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).getNick().contains(contents)) {
                    search_list.add(list.get(i));
                }
            }

            adapter.setList(search_list);
        } else {
            adapter.setList(list);
            isScroll = false;
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        binding.tvSort.setText(item.getTitle());
        list = new ArrayList<>();
        page = 1;
        getHomePopularList();


        switch (item.getItemId()) {
            case R.id.sort_heart:
            case R.id.sort_popularity:
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
