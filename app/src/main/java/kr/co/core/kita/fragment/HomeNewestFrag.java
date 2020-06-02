package kr.co.core.kita.fragment;


import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
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
import kr.co.core.kita.databinding.FragmentHomeNewestBinding;
import kr.co.core.kita.server.ReqBasic;
import kr.co.core.kita.server.netUtil.HttpResult;
import kr.co.core.kita.server.netUtil.NetUrls;
import kr.co.core.kita.util.AllOfDecoration;
import kr.co.core.kita.util.AppPreference;
import kr.co.core.kita.util.Common;
import kr.co.core.kita.util.StringUtil;

public class HomeNewestFrag extends BaseFrag {
    private FragmentHomeNewestBinding binding;
    private Activity act;


    private GridLayoutManager manager;
    private HomeMemberAdapter adapter;
    private ArrayList<HomeMemberData> list = new ArrayList<>();
    private boolean isScroll = false;
    private int page = 1;

    private String search = "";

    public void setSearchText(String searchText) {
        search = searchText;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home_newest, container, false);
        act = (AppCompatActivity) getActivity();

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
                        getHomeNewestList();
                    }
                }
            }
        });

        getHomeNewestList();

        return binding.getRoot();
    }


    private void getHomeNewestList() {
        isScroll = true;
        ReqBasic server = new ReqBasic(act, NetUrls.LIST_NEWEST) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {
                if (resultData.getResult() != null) {
                    try {
                        JSONObject jo = new JSONObject(resultData.getResult());

                        if (StringUtil.getStr(jo, "result").equalsIgnoreCase("Y") || StringUtil.getStr(jo, "result").equalsIgnoreCase(NetUrls.SUCCESS)) {
                            JSONArray ja = jo.getJSONArray("value");
                            for (int i = 0; i < ja.length(); i++) {
                                JSONObject job = ja.getJSONObject(i);
                                Log.i(StringUtil.TAG, "job: " + job);
                                String idx = StringUtil.getStr(job, "idx");
                                String loginYN = StringUtil.getStr(job, "loginYN");
                                String p_image1 = StringUtil.getStr(job, "p_image1");
                                String nick = StringUtil.getStr(job, "nick");
                                String intro = StringUtil.getStr(job, "intro");

                                list.add(new HomeMemberData(idx, nick, intro, p_image1, loginYN.equalsIgnoreCase("Y")));
                            }

                            if(StringUtil.isNull(search)) {
                                act.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        adapter.setList(list);
                                    }
                                });
                            } else {
                                setSearch(search);
                            }

                            isScroll = false;
                        } else {
                            if (page == 1) {
//                                Common.showToast(act, StringUtil.getStr(jo, "msg"));
                                Log.i(StringUtil.TAG, "msg: " + StringUtil.getStr(jo, "msg"));
                                Common.showToast(act, "There is no member.");
                            }

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

        server.setTag("Newest");
        server.addParams("midx", AppPreference.getProfilePref(act, AppPreference.PREF_MIDX));
        server.addParams("pn", String.valueOf(page));
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
}
