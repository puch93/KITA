package kr.co.core.kita.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
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
import kr.co.core.kita.databinding.ActivityHomeLocationBinding;
import kr.co.core.kita.server.ReqBasic;
import kr.co.core.kita.server.netUtil.HttpResult;
import kr.co.core.kita.server.netUtil.NetUrls;
import kr.co.core.kita.util.AllOfDecoration;
import kr.co.core.kita.util.AppPreference;
import kr.co.core.kita.util.Common;
import kr.co.core.kita.util.StringUtil;

public class HomeLocationAct extends AppCompatActivity {
    ActivityHomeLocationBinding binding;
    private Activity act;

    private GridLayoutManager manager;
    private HomeMemberAdapter adapter;
    private ArrayList<HomeMemberData> list = new ArrayList<>();
    private boolean isScroll = false;
    private int page = 1;

    String location = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_home_location, null);
        act = this;

        location = getIntent().getStringExtra("location");
        binding.tvTitle.setText(location);

        binding.flBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

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
                        getHomeLocationList();
                    }
                }
            }
        });

        getHomeLocationList();
    }

    private void getHomeLocationList() {
        isScroll = true;

        ReqBasic server = new ReqBasic(act, NetUrls.LIST_HOME_LOCATION) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {
                if (resultData.getResult() != null) {
                    try {
                        JSONObject jo = new JSONObject(resultData.getResult());

                        if( StringUtil.getStr(jo, "result").equalsIgnoreCase("Y") || StringUtil.getStr(jo, "result").equalsIgnoreCase(NetUrls.SUCCESS)) {
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

        server.setTag("Location");
        server.addParams("m_idx", AppPreference.getProfilePref(act, AppPreference.PREF_MIDX));
        server.addParams("location", location);
        server.addParams("pagenum", String.valueOf(page));
        server.execute(true, false);
    }

}
