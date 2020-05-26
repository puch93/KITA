package kr.co.core.kita.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;

import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import kr.co.core.kita.R;
import kr.co.core.kita.adapter.ProfileTalkAdapter;
import kr.co.core.kita.data.ProfileTalkData;
import kr.co.core.kita.databinding.ActivityProfileDetailBinding;
import kr.co.core.kita.dialog.WarningPopupDlg;
import kr.co.core.kita.server.ReqBasic;
import kr.co.core.kita.server.netUtil.HttpResult;
import kr.co.core.kita.server.netUtil.NetUrls;
import kr.co.core.kita.util.AllOfDecoration;
import kr.co.core.kita.util.AppPreference;
import kr.co.core.kita.util.Common;
import kr.co.core.kita.util.StringUtil;

public class ProfileDetailAct extends BaseAct implements View.OnClickListener {
    ActivityProfileDetailBinding binding;
    Activity act;

    ProfileTalkAdapter adapter;
    ArrayList<ProfileTalkData> list = new ArrayList<>();

    String yidx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_profile_detail, null);
        act = this;

        yidx = getIntent().getStringExtra("yidx");

        binding.flBack.setOnClickListener(this);
        binding.flReport.setOnClickListener(this);
        binding.llGift.setOnClickListener(this);
        binding.llChat.setOnClickListener(this);
        binding.llHeart.setOnClickListener(this);

        binding.recyclerView.setLayoutManager(new GridLayoutManager(act, 3));
        binding.recyclerView.setItemViewCacheSize(20);
        binding.recyclerView.setHasFixedSize(true);
        adapter = new ProfileTalkAdapter(act, list);
        binding.recyclerView.setAdapter(adapter);
        binding.recyclerView.addItemDecoration(new AllOfDecoration(act, AllOfDecoration.PROFILE_DETAIL));

        setTestData();

        getOtherInfo();
    }

    private void setTestData() {
        for (int i = 0; i < 20; i++) {
            list.add(new ProfileTalkData(null, null));
        }

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.setList(list);
            }
        });
    }

    private void getOtherInfo() {
        ReqBasic server = new ReqBasic(act, NetUrls.INFO_OTHER) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {
                if (resultData.getResult() != null) {
                    try {
                        JSONObject jo = new JSONObject(resultData.getResult());

                        if( StringUtil.getStr(jo, "result").equalsIgnoreCase("Y") || StringUtil.getStr(jo, "result").equalsIgnoreCase(NetUrls.SUCCESS)) {
                            JSONObject job = jo.getJSONObject("value");
                            String idx = StringUtil.getStr(job, "idx");
                            String nick = StringUtil.getStr(job, "nick");
                            String p_image1 = StringUtil.getStr(job, "p_image1");
                            String location = StringUtil.getStr(job, "location");
                            String location2 = StringUtil.getStr(job, "location2");

                            //TODO 추가 -- 선물 받은 개수 / 영상통화시 몇 peso 썼는지 / 내가 하트 했는지 여부

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    // 닉네임
                                    binding.tvNickTop.setText(nick);
                                    binding.tvNick.setText(nick);
                                    // 선물 받은 개수
                                    binding.tvGift.setText("0");
                                    // 영상통화시 몇 peso 썼는지
                                    binding.tvPayment.setText("0");
                                    // 내가 하트 했는지 여부
                                    binding.llHeart.setSelected(false);
                                    // 프로필 사진 등록
                                    if(!StringUtil.isNull(p_image1))
                                        Glide.with(act).load(p_image1).into(binding.ivProfile);
                                    else
                                        Glide.with(act).load(R.drawable.img_noimg02).into(binding.ivProfile);


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

        server.setTag("Other Info");
        server.addParams("m_idx", AppPreference.getProfilePref(act, AppPreference.PREF_MIDX));
        server.addParams("y_idx", yidx);
        server.execute(true, false);
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fl_report:
                startActivity(new Intent(act, WarningPopupDlg.class).putExtra("type", WarningPopupDlg.REPORT));
                break;
            case R.id.fl_back:
                finish();
                break;

            case R.id.ll_gift:
                startActivity(new Intent(act, GiftAct.class));
                break;

            case R.id.ll_chat:
                break;
            case R.id.ll_heart:
                break;

        }
    }
}
