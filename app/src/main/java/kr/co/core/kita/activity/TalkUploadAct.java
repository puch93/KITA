package kr.co.core.kita.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.listener.OnSelectedListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import kr.co.core.kita.R;
import kr.co.core.kita.adapter.TalkImageAdapter;
import kr.co.core.kita.databinding.ActivityTalkUploadBinding;
import kr.co.core.kita.server.ReqBasic;
import kr.co.core.kita.server.netUtil.HttpResult;
import kr.co.core.kita.server.netUtil.NetUrls;
import kr.co.core.kita.util.AllOfDecoration;
import kr.co.core.kita.util.AppPreference;
import kr.co.core.kita.util.Common;
import kr.co.core.kita.util.Glide4Engine;
import kr.co.core.kita.util.StringUtil;

public class TalkUploadAct extends AppCompatActivity {
    ActivityTalkUploadBinding binding;
    Activity act;

    TalkImageAdapter adapter;
    ArrayList<Uri> list = new ArrayList<>();

    int image_count = 0;
    List<Uri> mSelected;

    private static final int IMAGES = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_talk_upload, null);
        act = this;

        list.add(null);

        binding.flBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        binding.tvUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.etContents.length() == 0) {
                    Common.showToast(act, "Please fill in the blanks");
                } else if (image_count <= 0) {
                    Common.showToast(act, "Please register your photos");
                } else {
                    doTalkUpload();
                }
            }
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        binding.recyclerView.setLayoutManager(layoutManager);
        binding.recyclerView.setItemViewCacheSize(20);
        binding.recyclerView.setHasFixedSize(true);

        adapter = new TalkImageAdapter(act, list, new TalkImageAdapter.InterClickListener() {
            @Override
            public void selected() {
                if (image_count >= 20) {
                    Common.showToast(act, "Up to 20 photos can be registered");
                } else {
                    Matisse.from(act)
                            .choose(MimeType.ofImage())
                            .countable(true)
                            .maxSelectable(20 - image_count)
                            .imageEngine(new Glide4Engine())
                            .setOnSelectedListener(new OnSelectedListener() {
                                @Override
                                public void onSelected(@NonNull List<Uri> uriList, @NonNull List<String> pathList) {

                                }
                            })
                            .spanCount(4)
                            .forResult(IMAGES);
                }
            }

            @Override
            public void removed() {
                --image_count;
            }
        });
        binding.recyclerView.setAdapter(adapter);
        binding.recyclerView.addItemDecoration(new AllOfDecoration(act, AllOfDecoration.TALK_IMAGE_LIST));
    }

    private void doTalkUpload() {
        ReqBasic server = new ReqBasic(act, NetUrls.REGISTER_TALK) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {
                if (resultData.getResult() != null) {
                    try {
                        JSONObject jo = new JSONObject(resultData.getResult());

                        if (StringUtil.getStr(jo, "result").equalsIgnoreCase("Y") || StringUtil.getStr(jo, "result").equalsIgnoreCase(NetUrls.SUCCESS)) {

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

        server.setTag("Register Talk");
        server.addParams("m_idx", AppPreference.getProfilePref(act, AppPreference.PREF_MIDX));
        for (int i = 1; i < list.size(); i++) {
            File file = new File(StringUtil.getPath(act, list.get(i)));
            server.addFileParams("image" + i, file);
        }
        server.addParams("content", binding.etContents.getText().toString());
        server.execute(true, true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == IMAGES) {
                mSelected = Matisse.obtainResult(data);
                Log.e("TEST_HOME", "mSelected: " + mSelected);

                list.addAll(mSelected);
                Log.e("TEST_HOME", "list: " + list);
                image_count = list.size() - 1;

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.setList(list);
                    }
                });
            }
        }
    }
}
