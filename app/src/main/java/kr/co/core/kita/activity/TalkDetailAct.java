package kr.co.core.kita.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import kr.co.core.kita.R;
import kr.co.core.kita.adapter.CommentAdapter;
import kr.co.core.kita.adapter.ImagePagerAdapter;
import kr.co.core.kita.data.CommentData;
import kr.co.core.kita.databinding.ActivityTalkDetailBinding;
import kr.co.core.kita.server.ReqBasic;
import kr.co.core.kita.server.netUtil.HttpResult;
import kr.co.core.kita.server.netUtil.NetUrls;
import kr.co.core.kita.util.AppPreference;
import kr.co.core.kita.util.Common;
import kr.co.core.kita.util.StringUtil;

public class TalkDetailAct extends BaseAct implements View.OnClickListener {
    private ActivityTalkDetailBinding binding;
    private Activity act;

    private ImagePagerAdapter imagePagerAdapter;
    private ArrayList<String> imageList = new ArrayList<>();

    private CommentAdapter adapter;
    private ArrayList<CommentData> list = new ArrayList<>();

    private String t_idx = "";
    private String u_idx, u_nick, u_image, u_gender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_talk_detail, null);
        act = this;

        // 클릭 리스너
        binding.flBack.setOnClickListener(this);
        binding.flDelete.setOnClickListener(this);
        binding.flCommentReg.setOnClickListener(this);
        binding.tvChat.setOnClickListener(this);


        t_idx = getIntent().getStringExtra("t_idx");
        u_idx = getIntent().getStringExtra("u_idx");
        u_nick = getIntent().getStringExtra("u_nick");
        u_image = getIntent().getStringExtra("u_image");
        u_gender = getIntent().getStringExtra("u_gender");


        // 내 토크글 상세인지 상대 토크글 상세인지 확인인
        if (u_idx.equalsIgnoreCase(AppPreference.getProfilePref(act, AppPreference.PREF_MIDX)) || AppPreference.getProfilePref(act, AppPreference.PREF_GENDER).equalsIgnoreCase(u_gender)) {
            binding.llCommentArea.setVisibility(View.GONE);
            binding.tvChat.setVisibility(View.GONE);
            if (!StringUtil.isNull(getIntent().getStringExtra("from"))) {
                binding.flDelete.setVisibility(View.GONE);
            }
        } else {
            binding.flDelete.setVisibility(View.GONE);
        }

        //회원 데이터 세팅
        binding.tvNick.setText(u_nick);
        binding.tvNickTop.setText(u_nick);

        if (StringUtil.isNull(u_image)) {
            Glide.with(act)
                    .load(R.drawable.img_chatlist_noimg)
                    .transform(new CircleCrop())
                    .into(binding.ivProfile);
        } else {
            Glide.with(act)
                    .load(u_image)
                    .transform(new CircleCrop())
                    .into(binding.ivProfile);
        }

        // 뷰페이저 세팅
        /* set view pager height */
        binding.imagePager.post(new Runnable() {
            @Override
            public void run() {
                int height = binding.imagePager.getMeasuredWidth();
                Log.e(StringUtil.TAG, "getMeasuredWidth: " + height);
                height = (int) (binding.imagePager.getWidth() * 0.75);
                Log.e(StringUtil.TAG, "getWidth: " + height);
                if (height <= 0) {
                    height = getResources().getDimensionPixelSize(R.dimen.profile_detail_default_height);
                }
                FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) binding.imagePager.getLayoutParams();
                params.height = height;
                binding.imagePager.setLayoutParams(params);
            }
        });

        // 토크 상세정보 세팅
        getTalkDetail();

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(act));
        binding.recyclerView.setItemViewCacheSize(20);
        binding.recyclerView.setHasFixedSize(true);

        adapter = new CommentAdapter(act, list);
        binding.recyclerView.setAdapter(adapter);
    }

    private void doDeleteTalk() {
        ReqBasic server = new ReqBasic(act, NetUrls.TALK_DELETE) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {
                if (resultData.getResult() != null) {
                    try {
                        JSONObject jo = new JSONObject(resultData.getResult());

                        if (StringUtil.getStr(jo, "result").equalsIgnoreCase("Y") || StringUtil.getStr(jo, "result").equalsIgnoreCase(NetUrls.SUCCESS)) {
                            setResult(RESULT_OK);
                            finish();
                        } else {
//                            Common.showToast(act, StringUtil.getStr(jo, "msg"));
                            Log.i(StringUtil.TAG, "msg: " + StringUtil.getStr(jo, "msg"));
                            Common.showToastNetwork(act);
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

        server.setTag("Delete");
        server.addParams("t_idx", t_idx);
        server.addParams("m_idx", u_idx);
        server.execute(true, false);
    }

    private void getTalkDetail() {
        list = new ArrayList<>();
        ReqBasic server = new ReqBasic(act, NetUrls.TALK_DETAIL) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {
                if (resultData.getResult() != null) {
                    try {
                        JSONObject jo = new JSONObject(resultData.getResult());

                        if (StringUtil.getStr(jo, "result").equalsIgnoreCase("Y") || StringUtil.getStr(jo, "result").equalsIgnoreCase(NetUrls.SUCCESS)) {
                            JSONObject job = jo.getJSONObject("data");

                            //토크 이미지 세팅
                            for (int i = 1; i < 21; i++) {
                                String imageUrl = StringUtil.getStr(job, "t_image" + i);
                                if (StringUtil.isNull(imageUrl)) {
                                    break;
                                } else {
                                    imageList.add(imageUrl);
                                }
                            }

                            //토크 댓글 세팅
                            if (!StringUtil.isNull(job.getString("comment"))) {
                                JSONArray ja_comment = job.getJSONArray("comment");
                                for (int i = 0; i < ja_comment.length(); i++) {
                                    JSONObject job_comment = ja_comment.getJSONObject(i);

                                    String idx = StringUtil.getStr(job_comment, "u_idx");
                                    String comment = StringUtil.getStr(job_comment, "comment");
                                    String nick = StringUtil.getStr(job_comment, "nick");
                                    String p_image1 = StringUtil.getStr(job_comment, "p_image1");
                                    String regdate = StringUtil.converTime(StringUtil.getStr(job_comment, "regdate"), "yyyy.MM.dd hh:mm");

                                    list.add(new CommentData(idx, nick, comment, regdate, p_image1));
                                }
                            }

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    imagePagerAdapter = new ImagePagerAdapter(getSupportFragmentManager(), imageList);
                                    binding.imagePager.setAdapter(imagePagerAdapter);
                                    binding.pageIndicator.attachTo(binding.imagePager);

                                    binding.tvContents.setText(Common.decodeEmoji(StringUtil.getStr(job, "content")));
                                    binding.tvRegDate.setText(StringUtil.converTime(StringUtil.getStr(job, "tb_regdate"), "yyyy.MM.dd hh:mm"));
                                    binding.tvCountComment.setText(StringUtil.getStr(job, "tb_commentcnt"));

                                    adapter.setList(list);
                                }
                            });
                        } else {
                            Common.showToast(act, "This is a temporary error.");
                            finish();
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

        server.setTag("Talk Detail");
        server.addParams("t_idx", t_idx);
        server.execute(true, false);
    }

    private void doRegisterComment() {
        ReqBasic server = new ReqBasic(act, NetUrls.REGISTER_TALK_COMMENT) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {
                if (resultData.getResult() != null) {
                    try {
                        JSONObject jo = new JSONObject(resultData.getResult());

                        if (StringUtil.getStr(jo, "result").equalsIgnoreCase("Y") || StringUtil.getStr(jo, "result").equalsIgnoreCase(NetUrls.SUCCESS)) {
                            Common.showToast(act, "Registered successfully.");
                            getTalkDetail();

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    binding.etComment.setText("");
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

        server.setTag("Register Comment");
        server.addParams("t_idx", t_idx);
        server.addParams("m_idx", AppPreference.getProfilePref(act, AppPreference.PREF_MIDX));
        server.addParams("contents", binding.etComment.getText().toString());
        server.execute(true, false);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fl_back:
                finish();
                break;

            case R.id.fl_delete:
                showAlert(act, "Delete", "Are you sure you want to delete the post?", new OnAfterConnection() {
                    @Override
                    public void onAfter() {
                        doDeleteTalk();
                    }
                });
                break;

            case R.id.fl_comment_reg:
                if (binding.etComment.length() == 0) {
                    Common.showToast(act, "Please enter a comment.");
                } else {
                    doRegisterComment();
                }
                break;

            case R.id.tv_chat:
                doCheckRoom();
                break;
        }
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
                        act.startActivity(new Intent(act, ChatAct.class)
                                .putExtra("room_idx", room_idx)
                                .putExtra("yidx", u_idx)
                                .putExtra("otherImage", u_image)
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
        server.addParams("tidx", u_idx);
        server.execute(true, false);
    }
}
