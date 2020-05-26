package kr.co.core.kita.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;

import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import kr.co.core.kita.R;
import kr.co.core.kita.activity.GiftHistoryAct;
import kr.co.core.kita.activity.SettingAct;
import kr.co.core.kita.activity.TalkUploadAct;
import kr.co.core.kita.adapter.ProfileTalkAdapter;
import kr.co.core.kita.data.ProfileTalkData;
import kr.co.core.kita.databinding.FragmentMeBinding;
import kr.co.core.kita.server.ReqBasic;
import kr.co.core.kita.server.netUtil.HttpResult;
import kr.co.core.kita.server.netUtil.NetUrls;
import kr.co.core.kita.util.AllOfDecoration;
import kr.co.core.kita.util.AppPreference;
import kr.co.core.kita.util.Common;
import kr.co.core.kita.util.StringUtil;

public class MeFrag extends BaseFrag implements View.OnClickListener {
    FragmentMeBinding binding;
    Activity act;

    ProfileTalkAdapter adapter;
    ArrayList<ProfileTalkData> list = new ArrayList<>();


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_me, container, false);
        act = getActivity();

        binding.recyclerView.setLayoutManager(new GridLayoutManager(act, 3));
        binding.recyclerView.setItemViewCacheSize(20);
        binding.recyclerView.setHasFixedSize(true);
        adapter = new ProfileTalkAdapter(act, list);
        binding.recyclerView.setAdapter(adapter);
        binding.recyclerView.addItemDecoration(new AllOfDecoration(act, AllOfDecoration.PROFILE_DETAIL));

        setTestData();

        binding.flMenu.setOnClickListener(this);
        binding.llGiftArea.setOnClickListener(this);
        binding.tvUpload.setOnClickListener(this);

        getMyInfo();
        getMyTalkList();

        return binding.getRoot();
    }

    private void getMyTalkList() {
        ReqBasic server = new ReqBasic(act, NetUrls.LIST_TALK) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {
                if (resultData.getResult() != null) {
                    try {
                        JSONObject jo = new JSONObject(resultData.getResult());

                        if (StringUtil.getStr(jo, "result").equalsIgnoreCase("Y") || StringUtil.getStr(jo, "result").equalsIgnoreCase(NetUrls.SUCCESS)) {

                        } else {
//                            Common.showToast(act, StringUtil.getStr(jo, "msg"));
                            Log.i(StringUtil.TAG, "msg: " + StringUtil.getStr(jo, "msg"));
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

        server.setTag("Talk List");
        server.addParams("u_idx", AppPreference.getProfilePref(act, AppPreference.PREF_MIDX));
        server.execute(true, false);
    }

    private void getMyInfo() {
        ReqBasic server = new ReqBasic(act, NetUrls.INFO_ME) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {
                if (resultData.getResult() != null) {
                    try {
                        JSONObject jo = new JSONObject(resultData.getResult());

                        if (StringUtil.getStr(jo, "result").equalsIgnoreCase("Y") || StringUtil.getStr(jo, "result").equalsIgnoreCase(NetUrls.SUCCESS)) {
                            JSONObject job = jo.getJSONObject("value");
                            String nick = StringUtil.getStr(job, "nick");
                            String p_image1 = StringUtil.getStr(job, "p_image1");

                            //TODO 추가 -- 선물 받은 개수 / 영상통화시 몇 peso 썼는지

                            act.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    // 닉네임
                                    binding.tvNick.setText(nick);
                                    // 선물 받은 개수
                                    binding.tvGift.setText("0");
                                    // 영상통화시 몇 peso 썼는지
                                    binding.tvPayment.setText("0");
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

        server.setTag("My Info");
        server.addParams("u_idx", AppPreference.getProfilePref(act, AppPreference.PREF_MIDX));
        server.execute(true, false);
    }

    private void setTestData() {
        for (int i = 0; i < 20; i++) {
            list.add(new ProfileTalkData(null, null));
        }

        act.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.setList(list);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fl_menu:
                act.startActivity(new Intent(act, SettingAct.class));
                break;

            case R.id.ll_gift_area:
                act.startActivity(new Intent(act, GiftHistoryAct.class));
                break;

            case R.id.tv_upload:
                act.startActivity(new Intent(act, TalkUploadAct.class));
                break;
        }
    }
}
