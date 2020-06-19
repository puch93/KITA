package kr.co.core.kita.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.listener.OnSelectedListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
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

public class TalkUploadAct extends BaseAct {
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

        /* EditText 포커스될때 키보드가 UI 가리는 것 막음 */
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        /* EditText 포커스될때 ui 맨밑으로 */
        binding.scrollView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                if (bottom < oldBottom) {
                    binding.scrollView.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            binding.scrollView.scrollTo(0, binding.scrollView.getBottom());
                        }
                    }, 0);
                }
            }
        });


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
//                            Common.showToast(act, StringUtil.getStr(jo, "comment"));
                            Common.showToast(act, "written successfully");
                            setResult(RESULT_OK);
                            finish();
                        } else {
//                            Common.showToast(act, StringUtil.getStr(jo, "comment"));
                            Log.i(StringUtil.TAG, "msg: " + StringUtil.getStr(jo, "comment"));
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


        try {
            server.setTag("Register Talk");
            server.addParams("m_idx", AppPreference.getProfilePref(act, AppPreference.PREF_MIDX));
            for (int i = 1; i < list.size(); i++) {
                String realPath = StringUtil.getPath(act, list.get(i));
                File file = new File(realPath);
                long fileSize = file.length();
                Log.i(StringUtil.TAG, "file size: " + file.length());
                // 10485760 용량제한
                server.addFileParams("image" + i, file);

//            if (fileSize > 10485760) {
//                BitmapFactory.Options options = new BitmapFactory.Options();
//                options.inSampleSize = 2;
//                Bitmap bm = BitmapFactory.decodeFile(realPath, options);
//
//                Bitmap resize = null;
//                try {
//                    File resize_file = new File(realPath);
//                    FileOutputStream out = new FileOutputStream(resize_file);
//
//                    int width = bm.getWidth();
//                    int height = bm.getHeight();
//
//                    if (width > 1024) {
//                        int resizeHeight = 0;
//                        if (height > 768) {
//                            resizeHeight = 768;
//                        } else {
//                            resizeHeight = height / (width / 1024);
//                        }
//
//                        resize = Bitmap.createScaledBitmap(bm, 1024, resizeHeight, true);
//                        resize.compress(Bitmap.CompressFormat.PNG, 100, out);
//                    } else {
//                        resize = Bitmap.createScaledBitmap(bm, width, height, true);
//                        resize.compress(Bitmap.CompressFormat.PNG, 100, out);
//                    }
//                } catch (FileNotFoundException e) {
//                    e.printStackTrace();
//                }
//            } else {
//                server.addFileParams("image" + i, file);
//            }
            }
            server.addParams("content", URLEncoder.encode(binding.etContents.getText().toString(),"UTF-8"));
            server.execute(true, true);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
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
