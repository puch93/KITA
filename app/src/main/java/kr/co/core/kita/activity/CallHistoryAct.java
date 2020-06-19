package kr.co.core.kita.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import kr.co.core.kita.R;
import kr.co.core.kita.adapter.HistoryCallAdapter;
import kr.co.core.kita.data.HistoryCallData;
import kr.co.core.kita.databinding.ActivityCallHistoryBinding;
import kr.co.core.kita.server.ReqBasic;
import kr.co.core.kita.server.netUtil.HttpResult;
import kr.co.core.kita.server.netUtil.NetUrls;
import kr.co.core.kita.util.AllOfDecoration;
import kr.co.core.kita.util.AppPreference;
import kr.co.core.kita.util.Common;
import kr.co.core.kita.util.StringUtil;

public class CallHistoryAct extends BaseAct {
    ActivityCallHistoryBinding binding;
    Activity act;

    HistoryCallAdapter adapter;
    ArrayList<HistoryCallData> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_call_history, null);
        act = this;


        binding.recyclerView.setLayoutManager(new GridLayoutManager(act, 2));
        binding.recyclerView.setHasFixedSize(true);
        binding.recyclerView.setItemViewCacheSize(20);
        adapter = new HistoryCallAdapter(act, list);
        binding.recyclerView.setAdapter(adapter);
        binding.recyclerView.addItemDecoration(new AllOfDecoration(act, AllOfDecoration.HISTORY_CALL_LIST));

        binding.flBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        getCallHistory();
    }

    private void getCallHistory() {
        list = new ArrayList<>();
        ReqBasic server = new ReqBasic(act, NetUrls.LIST_CALL_HISTORY) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {
                if (resultData.getResult() != null) {
                    try {
                        JSONObject jo = new JSONObject(resultData.getResult());

                        if (StringUtil.getStr(jo, "result").equalsIgnoreCase("Y") || StringUtil.getStr(jo, "result").equalsIgnoreCase(NetUrls.SUCCESS)) {
                            JSONArray ja = jo.getJSONArray("list");
                            for (int i = 0; i < ja.length(); i++) {
                                JSONObject job = ja.getJSONObject(i);
                                String vcl_idx = StringUtil.getStr(job, "vcl_idx");
                                String u_idx = StringUtil.getStr(job, "u_idx");
                                String t_idx = StringUtil.getStr(job, "t_idx");
                                String vc_type = StringUtil.getStr(job, "vc_type");
                                String vcl_sdate = StringUtil.getStr(job, "vcl_sdate");
                                String vcl_edate = StringUtil.getStr(job, "vcl_edate");
                                String vc_refidx = StringUtil.getStr(job, "vc_refidx");
                                String p_image1 = StringUtil.getStr(job, "p_image1");
                                String nick = StringUtil.getStr(job, "nick");
                                String intro = Common.decodeEmoji(StringUtil.getStr(job, "intro"));
                                String loginYN = StringUtil.getStr(job, "loginYN");
                                String location = StringUtil.getStr(job, "location");

                                list.add(new HistoryCallData(vcl_idx, t_idx, nick, p_image1,location, loginYN.equalsIgnoreCase("Y"), StringUtil.convertDateFormat(vcl_sdate, vcl_edate)));
                            }

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    adapter.setList(list);
                                }
                            });
                        } else {

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Common.showToastNetwork(act);
                    }
                } else {
                    Common.showToastNetwork(act);
                }
            }
        };

        server.setTag("Call History");
        server.addParams("m_idx", AppPreference.getProfilePref(act, AppPreference.PREF_MIDX));
        server.execute(true, false);
    }
}
