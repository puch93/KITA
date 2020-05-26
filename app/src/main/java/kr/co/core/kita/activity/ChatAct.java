package kr.co.core.kita.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.ArrayList;

import kr.co.core.kita.R;
import kr.co.core.kita.activity.rtc.ConnectActivity;
import kr.co.core.kita.adapter.ChatAdapter;
import kr.co.core.kita.data.ChattingData;
import kr.co.core.kita.databinding.ActivityChatBinding;
import kr.co.core.kita.dialog.PictureDlg;
import kr.co.core.kita.util.Common;

public class ChatAct extends AppCompatActivity implements View.OnClickListener {
    ActivityChatBinding binding;
    Activity act;

    private ArrayList<ChattingData> list = new ArrayList<>();
    private ChatAdapter adapter;

    private String otherImage;
    public static String room_idx = "";


    /* 이미지 보내기 관련 */
    private Uri photoUri;
    private String mImgFilePath;
    private static final int PICK_DIALOG = 1000;
    private static final int PHOTO_GALLERY = 1001;
    private static final int PHOTO_TAKE = 1002;
    private static final int PHOTO_CROP = 1003;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_chat, null);
        act = this;

        room_idx = getIntent().getStringExtra("room_idx");
        otherImage = getIntent().getStringExtra("otherImage");

        setLayout();
    }

    private void setLayout() {
        // set click listener
        binding.flBack.setOnClickListener(this);
        binding.flMore.setOnClickListener(this);
        binding.flCall.setOnClickListener(this);
        binding.flSend.setOnClickListener(this);


        // set recycler view
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(act));

        adapter = new ChatAdapter(act, room_idx, list, otherImage);
        binding.recyclerView.setAdapter(adapter);
        binding.recyclerView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                if (bottom < oldBottom) {
                    binding.recyclerView.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            binding.recyclerView.scrollToPosition(adapter.getItemCount() - 1);
                        }
                    }, 100);
                }
            }
        });

        setTestData();
    }

    private void setTestData() {
        list.add(new ChattingData("2", "dateline", "15:06", "today", "hi~ It is content...", false));
        list.add(new ChattingData("1", "text", "15:06", null, "hi~ It is content...", true));
        list.add(new ChattingData("2", "text", "15:06", null, "hi~ It is content...", true));


        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.setList(list);
            }
        });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fl_send:
                if (binding.etContents.length() == 0) {
                    Common.showToast(act, "내용을 입력해주세요");
                } else {
                    list.add(new ChattingData("1", "text", "15:06", null, binding.etContents.getText().toString(), false));
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter.setList(list);
                            binding.recyclerView.smoothScrollToPosition(adapter.getItemCount());
                        }
                    });

                    binding.etContents.setText("");
                }
                break;

            case R.id.fl_back:
                finish();
                break;

            case R.id.fl_more:
//                startActivityForResult(new Intent(act, PictureDlg.class), PICK_DIALOG);
                Intent intent2 = new Intent(act, ConnectActivity.class);
                intent2.putExtra("type", "receive");
                intent2.putExtra("test", binding.etContents.getText().toString());
                startActivity(intent2);

                break;

            case R.id.fl_call:
                Intent intent = new Intent(act, ConnectActivity.class);
                intent.putExtra("type", "call");
                intent.putExtra("test", binding.etContents.getText().toString());
                startActivity(intent);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PICK_DIALOG:
                    if (data.getStringExtra("type").equalsIgnoreCase(PictureDlg.CAMERA)) {
                        Common.showToast(act, "camera");
                    } else {
                        Common.showToast(act, "gallery");
                    }
                    break;

            }
        }
    }
}
