package kr.co.core.kita.activity;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import kr.co.core.kita.R;
import kr.co.core.kita.adapter.CommentAdapter;
import kr.co.core.kita.databinding.ActivityJoinBinding;
import kr.co.core.kita.dialog.SelectDlg;
import kr.co.core.kita.util.Common;

public class JoinAct extends BaseAct implements View.OnClickListener {
    ActivityJoinBinding binding;
    Activity act;

    private static final int SELECT = 101;
    TextView selectedView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_join, null);
        act = this;

        binding.flBack.setOnClickListener(this);
        binding.tvRecentWork01.setOnClickListener(this);
        binding.tvGender.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent = null;

        switch (v.getId()) {
            case R.id.fl_back:
                finish();
                break;

            case R.id.tv_recent_work_01:
                selectedView = (TextView) v;

                if(binding.tvGender.length() == 0) {
                    Common.showToast(act, "Please select your gender first");
                } else {
                    intent = new Intent(act, SelectDlg.class);
                    intent.putExtra("type", binding.tvGender.getText().toString());
                    intent.putExtra("data", binding.tvRecentWork01.getText().toString());
                    startActivityForResult(intent, SELECT);
                }
                break;

            case R.id.tv_gender:
                selectedView = (TextView) v;

                intent = new Intent(act, SelectDlg.class);
                intent.putExtra("type", SelectDlg.TYPE_GENDER);
                intent.putExtra("data", binding.tvGender.getText().toString());
                startActivityForResult(intent, SELECT);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK) {

            String value = data.getStringExtra("value");

            switch (requestCode) {
                case SELECT:
                    if(selectedView != null) {
                        selectedView.setText(value);
                    }
                    break;
            }
        }
    }
}
