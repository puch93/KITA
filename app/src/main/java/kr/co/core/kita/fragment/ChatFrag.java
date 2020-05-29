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
import androidx.recyclerview.widget.LinearLayoutManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import kr.co.core.kita.R;
import kr.co.core.kita.activity.CallHistoryAct;
import kr.co.core.kita.activity.SearchAct;
import kr.co.core.kita.adapter.ChatListAdapter;
import kr.co.core.kita.data.ChattingLIstData;
import kr.co.core.kita.databinding.FragmentChatBinding;
import kr.co.core.kita.server.ReqBasic;
import kr.co.core.kita.server.netUtil.HttpResult;
import kr.co.core.kita.server.netUtil.NetUrls;
import kr.co.core.kita.util.AppPreference;
import kr.co.core.kita.util.Common;
import kr.co.core.kita.util.StringUtil;

import static android.app.Activity.RESULT_OK;
import static com.zhihu.matisse.MimeType.isImage;

public class ChatFrag extends BaseFrag {
    FragmentChatBinding binding;
    Activity act;

    LinearLayoutManager manager;
    ChatListAdapter adapter;
    ArrayList<ChattingLIstData> list = new ArrayList<>();

    private String search_result = "";

    private static final int SEARCH = 101;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_chat, container, false);
        act = getActivity();

        manager = new LinearLayoutManager(act);
        binding.recyclerView.setLayoutManager(manager);
        binding.recyclerView.setHasFixedSize(true);
        binding.recyclerView.setItemViewCacheSize(20);
        adapter = new ChatListAdapter(act, list);
        binding.recyclerView.setAdapter(adapter);


        binding.flHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(act, CallHistoryAct.class));
            }
        });

        binding.flSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(act, SearchAct.class).putExtra("data", search_result), SEARCH);
            }
        });

        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        getChattingList();
    }

    private void getChattingList() {
        list = new ArrayList<>();
        ReqBasic server = new ReqBasic(act, NetUrls.LIST_CHAT) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {
                if (resultData.getResult() != null) {
                    try {
                        JSONObject jo = new JSONObject(resultData.getResult());

                        if (StringUtil.getStr(jo, "result").equalsIgnoreCase("Y") || StringUtil.getStr(jo, "result").equalsIgnoreCase(NetUrls.SUCCESS)) {
                            StringUtil.logLargeString(jo.toString());

                            JSONObject jo_chat = jo.getJSONObject("data");
                            JSONArray ja = jo_chat.getJSONArray("chats");
                            for (int i = 0; i < ja.length(); i++) {
                                JSONObject job = ja.getJSONObject(i);
                                Log.i(StringUtil.TAG, "job(" + i + "): " + job);

                                String no_read_count = StringUtil.getStr(job, "nonereadmsgcnt");
                                String contents = StringUtil.getStr(job, "msg");
                                if (isImage(contents)) {
                                    contents = "이미지";
                                }
                                String date = StringUtil.getStr(job, "created_at");
                                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                date = Common.formatImeString(format.parse(date), act);

                                String room_idx = StringUtil.getStr(job, "room_idx");

                                /* 회원정보 (상대) */
                                if (job.has("friend")) {
                                    JSONArray other_array = job.getJSONArray("friend");
                                    String other_string = other_array.toString();

                                    if (!other_string.equalsIgnoreCase("[\"\"]")) {
                                        JSONObject other = other_array.getJSONObject(0);
                                        String idx = StringUtil.getStr(other, "idx");
                                        String nick = StringUtil.getStr(other, "nick");

                                        //프로필 사진관련
                                        String profile_img = StringUtil.getStr(other, "p_image1");

                                        list.add(new ChattingLIstData(idx, nick, profile_img, contents, date, no_read_count, room_idx));
                                    }
                                }
                            }

                            act.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    adapter.setList(list);
                                }
                            });
                        } else {

                        }

                    } catch (JSONException | ParseException e) {
                        e.printStackTrace();
                        Common.showToastNetwork(act);
                    }
                } else {
                    Common.showToastNetwork(act);
                }
            }
        };

        server.setTag("Chatting List");
        server.addParams("m_idx", AppPreference.getProfilePref(act, AppPreference.PREF_MIDX));
        server.execute(true, false);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case SEARCH:
                    search_result = data.getStringExtra("value");

                    if (!StringUtil.isNull(search_result)) {
                        ArrayList<ChattingLIstData> search_list = new ArrayList<>();
                        for (int i = 0; i < list.size(); i++) {
                            if (list.get(i).getContents().contains(search_result)) {
                                search_list.add(list.get(i));
                            }
                        }

                        adapter.setList(search_list);
                    } else {
                        adapter.setList(list);
                    }
                    break;
            }
        }
    }
}
