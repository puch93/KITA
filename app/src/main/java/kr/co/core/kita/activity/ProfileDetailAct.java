package kr.co.core.kita.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import kr.co.core.kita.R;
import kr.co.core.kita.adapter.ProfileTalkAdapter;
import kr.co.core.kita.data.ProfileTalkData;
import kr.co.core.kita.databinding.ActivityProfileDetailBinding;
import kr.co.core.kita.dialog.ReportDlg;
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

    String yidx, nick, p_image1;

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
        binding.ivProfile.setOnClickListener(this);

        binding.recyclerView.setLayoutManager(new GridLayoutManager(act, 3));
        binding.recyclerView.setItemViewCacheSize(20);
        binding.recyclerView.setHasFixedSize(true);
        adapter = new ProfileTalkAdapter(act, list);
        binding.recyclerView.setAdapter(adapter);
        binding.recyclerView.addItemDecoration(new AllOfDecoration(act, AllOfDecoration.PROFILE_DETAIL));


        getOtherInfo();
        getOtherTalkList();
    }


    private void getOtherInfo() {
        ReqBasic server = new ReqBasic(act, NetUrls.INFO_OTHER) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {
                if (resultData.getResult() != null) {
                    try {
                        JSONObject jo = new JSONObject(resultData.getResult());

                        if (StringUtil.getStr(jo, "result").equalsIgnoreCase("Y") || StringUtil.getStr(jo, "result").equalsIgnoreCase(NetUrls.SUCCESS)) {
                            JSONObject job = jo.getJSONObject("value");
                            String idx = StringUtil.getStr(job, "idx");
                            nick = StringUtil.getStr(job, "nick");
                            p_image1 = StringUtil.getStr(job, "p_image1");
                            String location = StringUtil.getStr(job, "location");
                            String location2 = StringUtil.getStr(job, "location2");
                            boolean heart = StringUtil.getStr(job, "heart_send").equalsIgnoreCase("Y");

                            //TODO 추가 -- 선물 받은 개수 / 영상통화시 몇 peso 썼는지

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
                                    binding.llHeart.setSelected(heart);
                                    // 프로필 사진 등록
                                    if (!StringUtil.isNull(p_image1))
                                        Glide.with(act).load(p_image1).into(binding.ivProfile);
                                    else
                                        Glide.with(act).load(R.drawable.img_noimg02).into(binding.ivProfile);

                                    adapter.setInfo(idx, nick, p_image1);
                                }
                            });
                        } else {

                        }

                        // 선물받은개수 가져오기
                        getGiftHistory();

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


    private void getOtherTalkList() {
        list = new ArrayList<>();

        ReqBasic server = new ReqBasic(act, NetUrls.LIST_TALK) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {
                if (resultData.getResult() != null) {
                    try {
                        JSONObject jo = new JSONObject(resultData.getResult());

                        if (StringUtil.getStr(jo, "result").equalsIgnoreCase("Y") || StringUtil.getStr(jo, "result").equalsIgnoreCase(NetUrls.SUCCESS)) {
                            JSONArray ja = jo.getJSONArray("data");
                            for (int i = 0; i < ja.length(); i++) {
                                JSONObject job = ja.getJSONObject(i);
                                String u_idx = StringUtil.getStr(job, "u_idx");
                                String tb_idx = StringUtil.getStr(job, "tb_idx");
                                String thumb_image = StringUtil.getStr(job, "thumb_image");
                                list.add(new ProfileTalkData(u_idx, tb_idx, thumb_image));
                            }
                        } else {
//                            Common.showToast(act, StringUtil.getStr(jo, "msg"));
                            Log.i(StringUtil.TAG, "msg: " + StringUtil.getStr(jo, "msg"));
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

        server.setTag("Talk List");
        server.addParams("u_idx", yidx);
        server.execute(true, false);
    }

    private void doHeartSwitch() {
        ReqBasic server = new ReqBasic(act, NetUrls.HEART) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {
                if (resultData.getResult() != null) {
                    try {
                        JSONObject jo = new JSONObject(resultData.getResult());

                        if (StringUtil.getStr(jo, "result").equalsIgnoreCase("Y") || StringUtil.getStr(jo, "result").equalsIgnoreCase(NetUrls.SUCCESS)) {
                            getOtherInfo();
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

        server.setTag("Heart");
        server.addParams("m_idx", AppPreference.getProfilePref(act, AppPreference.PREF_MIDX));
        server.addParams("y_idx", yidx);
        server.execute(true, false);
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
                            act.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    binding.tvGift.setText(String.valueOf(ja.length()));
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

        server.setTag("gifted" + " History");
        server.addParams("m_idx", yidx);
        server.addParams("type", "gifted");
        server.execute(true, false);
    }

    private void doCheckRoom() {
        ReqBasic server = new ReqBasic(act, NetUrls.CREATE_ROOM) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {
                if (resultData.getResult() != null) {
                    try {
                        JSONObject jo = new JSONObject(resultData.getResult());
                        JSONObject job = jo.getJSONObject("data");
                        String room_idx = StringUtil.getStr(job, "room_idx");
                        startActivity(new Intent(act, ChatAct.class)
                                .putExtra("room_idx", room_idx)
                                .putExtra("yidx", yidx)
                                .putExtra("otherImage", p_image1)
                        );

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Common.showToastNetwork(act);
                    }
                } else {
                    Common.showToastNetwork(act);
                }
            }
        };

        server.setTag("Create Room");
        server.addParams("type", "common");
        server.addParams("uidx", AppPreference.getProfilePref(act, AppPreference.PREF_MIDX));
        server.addParams("tidx", yidx);
        server.execute(true, false);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_profile:
                if(!StringUtil.isNull(p_image1)) {
                    startActivity(new Intent(act, EnlargeAct.class).putExtra("imageUrl", p_image1));
                }
                break;
            case R.id.fl_report:
                startActivity(new Intent(act, ReportDlg.class).putExtra("yidx", yidx));
                break;
            case R.id.fl_back:
                finish();
                break;

            case R.id.ll_gift:
                startActivity(new Intent(act, GiftAct.class).putExtra("yidx", yidx).putExtra("nick", nick));
                break;

            case R.id.ll_chat:
                doCheckRoom();
                break;
            case R.id.ll_heart:
                doHeartSwitch();
                break;

        }
    }
}
