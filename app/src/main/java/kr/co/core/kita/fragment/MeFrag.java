package kr.co.core.kita.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import kr.co.core.kita.R;
import kr.co.core.kita.activity.EnlargeAct;
import kr.co.core.kita.activity.GiftHistoryAct;
import kr.co.core.kita.activity.PaymentAct;
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

import static android.app.Activity.RESULT_OK;

public class MeFrag extends BaseFrag implements View.OnClickListener {
    FragmentMeBinding binding;
    Activity act;

    ProfileTalkAdapter adapter;
    ArrayList<ProfileTalkData> list = new ArrayList<>();


    private static final int SETTING = 1001;
    private static final int TALK_UPLOAD = 1002;
    private static final int PAYMENT = 1003;
    public static final int TALK_DETAIL = 101;

    private String p_image1 = "";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_me, container, false);
        act = getActivity();

        binding.recyclerView.setLayoutManager(new GridLayoutManager(act, 3));
        binding.recyclerView.setItemViewCacheSize(20);
        binding.recyclerView.setHasFixedSize(true);
        adapter = new ProfileTalkAdapter(act, list);
        adapter.setMeFrag(this);
        binding.recyclerView.setAdapter(adapter);
        binding.recyclerView.addItemDecoration(new AllOfDecoration(act, AllOfDecoration.PROFILE_DETAIL));


        /* set click listener */
        binding.flMenu.setOnClickListener(this);
        binding.ivProfile.setOnClickListener(this);
        binding.llGiftArea.setOnClickListener(this);
        binding.tvUpload.setOnClickListener(this);
        binding.llPointArea.setOnClickListener(this);

        /* set image height */
        binding.ivProfile.post(new Runnable() {
            @Override
            public void run() {
                int height = binding.ivProfile.getMeasuredWidth();
                Log.e(StringUtil.TAG, "getMeasuredWidth: " + height);
                height = (int) (binding.ivProfile.getWidth() * 0.75);
                Log.e(StringUtil.TAG, "getWidth: " + height);
                if (height <= 0) {
                    height = getResources().getDimensionPixelSize(R.dimen.profile_me_default_height);
                }
                FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) binding.ivProfile.getLayoutParams();
                params.height = height;
                binding.ivProfile.setLayoutParams(params);
            }
        });

        getMyInfo();
        getMyTalkList();

        return binding.getRoot();
    }

    private void getMyTalkList() {
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
                            p_image1 = StringUtil.getStr(job, "p_image1");
                            String peso = StringUtil.getStr(job, "peso");

                            //TODO 추가 -- 선물 받은 개수 / 영상통화시 몇 peso 썼는지

                            act.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    // 닉네임
                                    binding.tvNick.setText(nick);
                                    // 선물 받은 개수
                                    binding.tvGift.setText("0");
                                    // 영상통화시 몇 peso 썼는지
                                    binding.tvPhp.setText(peso);
                                    // 프로필 사진 등록
                                    if (!StringUtil.isNull(p_image1))
                                        Glide.with(act).load(p_image1).into(binding.ivProfile);
                                    else
                                        Glide.with(act).load(R.drawable.img_noimg02).into(binding.ivProfile);

                                    adapter.setInfo(AppPreference.getProfilePref(act, AppPreference.PREF_MIDX), nick, p_image1, AppPreference.getProfilePref(act, AppPreference.PREF_GENDER));
                                }
                            });
                        } else {

                        }

                        //선물받은 개수 가져오기
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

        server.setTag("My Info");
        server.addParams("u_idx", AppPreference.getProfilePref(act, AppPreference.PREF_MIDX));
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
        server.addParams("m_idx", AppPreference.getProfilePref(act, AppPreference.PREF_MIDX));
        server.addParams("type", "gifted");
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
            case R.id.fl_menu:
                startActivityForResult(new Intent(act, SettingAct.class), SETTING);
                break;

            case R.id.ll_gift_area:
                act.startActivity(new Intent(act, GiftHistoryAct.class));
                break;

            case R.id.tv_upload:
                startActivityForResult(new Intent(act, TalkUploadAct.class), TALK_UPLOAD);
                break;

            case R.id.ll_point_area:
                startActivityForResult(new Intent(act, PaymentAct.class), PAYMENT);
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PAYMENT:
                case SETTING:
                    getMyInfo();
                    break;

                case TALK_DETAIL:
                case TALK_UPLOAD:
                    Log.i(StringUtil.TAG, "onActivityResult: ");
                    getMyTalkList();
                    break;
            }
        }
    }
}
