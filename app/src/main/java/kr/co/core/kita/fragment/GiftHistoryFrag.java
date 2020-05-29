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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import kr.co.core.kita.R;
import kr.co.core.kita.adapter.HistoryGiftAdapter;
import kr.co.core.kita.data.HistoryGiftData;
import kr.co.core.kita.databinding.FragmentGiftHistoryBinding;
import kr.co.core.kita.server.ReqBasic;
import kr.co.core.kita.server.netUtil.HttpResult;
import kr.co.core.kita.server.netUtil.NetUrls;
import kr.co.core.kita.util.AllOfDecoration;
import kr.co.core.kita.util.AppPreference;
import kr.co.core.kita.util.Common;
import kr.co.core.kita.util.StringUtil;

public class GiftHistoryFrag extends BaseFrag {
    private FragmentGiftHistoryBinding binding;
    private Activity act;


    private LinearLayoutManager manager;
    private HistoryGiftAdapter adapter;
    private ArrayList<HistoryGiftData> list = new ArrayList<>();

    private String type = "";

    public void setType(String type) {
        this.type = type;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_gift_history, container, false);
        act = (AppCompatActivity) getActivity();

        // set recycler view
        manager = new LinearLayoutManager(act);
        binding.recyclerView.setLayoutManager(manager);
        binding.recyclerView.setItemViewCacheSize(20);
        binding.recyclerView.setHasFixedSize(true);

        adapter = new HistoryGiftAdapter(act, list);
        binding.recyclerView.setAdapter(adapter);
        binding.recyclerView.addItemDecoration(new AllOfDecoration(act, AllOfDecoration.HISTORY_GIFT_LIST));

        getGiftHistory();

        return binding.getRoot();
    }

    private void getGiftHistory() {
        list = new ArrayList<>();
        ReqBasic server = new ReqBasic(act, NetUrls.GIFT_HISTORY) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {
                if (resultData.getResult() != null) {
                    try {
                        JSONObject jo = new JSONObject(resultData.getResult());

                        if (StringUtil.getStr(jo, "result").equalsIgnoreCase("Y") || StringUtil.getStr(jo, "result").equalsIgnoreCase(NetUrls.SUCCESS)) {
                            JSONArray ja = jo.getJSONArray("data");
                            for (int i = 0; i < ja.length(); i++) {
                                JSONObject job = ja.getJSONObject(i);

                                String gi_idx = StringUtil.getStr(job, "gi_idx");
                                String t_idx = StringUtil.getStr(job, "t_idx");
                                String gi_name = StringUtil.getStr(job, "gi_name");
                                String gi_price = StringUtil.getStr(job, "gi_price");
                                String p_image1 = StringUtil.getStr(job, "p_image1");
                                String nick = StringUtil.getStr(job, "nick");

                                list.add(new HistoryGiftData(gi_idx, t_idx, p_image1, nick, gi_name, gi_price));
                            }
                        } else {

                        }

                        act.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                adapter.setList(list);
                            }
                        });

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Common.showToastNetwork(act);
                    }
                } else {
                    Common.showToastNetwork(act);
                }
            }
        };

        server.setTag(type + " History");
        server.addParams("m_idx", AppPreference.getProfilePref(act, AppPreference.PREF_MIDX));
        server.addParams("type", type);
        server.execute(true, false);
    }
}
