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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import kr.co.core.kita.R;
import kr.co.core.kita.adapter.TalkListAdapter;
import kr.co.core.kita.data.TalkListData;
import kr.co.core.kita.databinding.FragmentTalkSubBinding;
import kr.co.core.kita.server.ReqBasic;
import kr.co.core.kita.server.netUtil.HttpResult;
import kr.co.core.kita.server.netUtil.NetUrls;
import kr.co.core.kita.util.AllOfDecoration;
import kr.co.core.kita.util.AppPreference;
import kr.co.core.kita.util.Common;
import kr.co.core.kita.util.StringUtil;

public class TalkSubFrag extends BaseFrag {
    private FragmentTalkSubBinding binding;
    private Activity act;


    private LinearLayoutManager manager;
    private TalkListAdapter adapter;
    private ArrayList<TalkListData> list = new ArrayList<>();
    private boolean isScroll = false;
    private int page = 1;

    private String type = "";

    public void setType(String type) {
        this.type = type;
    }

    private String search ="";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_talk_sub, container, false);
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
                        ++page;
                        getTalkList();
                    }
                }
            }
        });

        getTalkList();

        return binding.getRoot();
    }

    private void getTalkList() {
        isScroll = true;
        ReqBasic server = new ReqBasic(act, NetUrls.LIST_TALK_ALL) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {
                if (resultData.getResult() != null) {
                    try {
                        JSONObject jo = new JSONObject(resultData.getResult());

                        if (StringUtil.getStr(jo, "result").equalsIgnoreCase("Y") || StringUtil.getStr(jo, "result").equalsIgnoreCase(NetUrls.SUCCESS)) {
                            JSONArray ja = jo.getJSONArray("data");
                            for (int i = 0; i < ja.length(); i++) {
                                JSONObject job = ja.getJSONObject(i);
                                String tb_idx = StringUtil.getStr(job, "tb_idx");
                                String content = StringUtil.getStr(job, "content");
                                String tb_regdate = StringUtil.converTime(StringUtil.getStr(job, "tb_regdate"), "yyyy.MM.dd");

                                String u_idx = StringUtil.getStr(job, "u_idx");
                                String nick = StringUtil.getStr(job, "nick");
                                String gender = StringUtil.getStr(job, "gender");
                                String p_image1 = StringUtil.getStr(job, "p_image1");

                                list.add(new TalkListData(tb_idx, u_idx, nick, gender, content, tb_regdate, p_image1));
                            }

                            act.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    isScroll = false;
                                    adapter.setList(list);
                                }
                            });
                        } else {
                            isScroll = true;
//                            Common.showToast(act, StringUtil.getStr(jo, "msg"));
                            Log.i(StringUtil.TAG, "msg: " + StringUtil.getStr(jo, "msg"));
                        }

                    } catch (JSONException e) {
                        isScroll = false;
                        e.printStackTrace();
                        Common.showToastNetwork(act);
                    }
                } else {
                    isScroll = false;
                    Common.showToastNetwork(act);
                }
            }
        };

        server.setTag("Talk List All");
        server.addParams("u_idx", AppPreference.getProfilePref(act, AppPreference.PREF_MIDX));
        server.addParams("type", type);
        server.addParams("pagenum", String.valueOf(page));
        server.addParams("search", search);
        server.execute(true, false);
    }

    public void setSearch(String contents) {
        search = contents;
        page = 1;
        list = new ArrayList<>();

        getTalkList();


//        if (!StringUtil.isNull(contents)) {
//            isScroll = true;
//            ArrayList<TalkListData> search_list = new ArrayList<>();
//            for (int i = 0; i < list.size(); i++) {
//                if (list.get(i).getContents().contains(contents)) {
//                    search_list.add(list.get(i));
//                }
//            }
//
//            adapter.setList(search_list);
//        } else {
//            adapter.setList(list);
//            isScroll = false;
//        }
    }


}
