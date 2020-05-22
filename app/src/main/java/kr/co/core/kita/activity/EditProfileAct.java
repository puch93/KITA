package kr.co.core.kita.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import kr.co.core.kita.R;
import kr.co.core.kita.databinding.ActivityEditProfileBinding;
import kr.co.core.kita.dialog.SelectDlg;
import kr.co.core.kita.util.AppPreference;
import kr.co.core.kita.util.Common;

public class EditProfileAct extends BaseAct implements View.OnClickListener {
    ActivityEditProfileBinding binding;
    Activity act;

    private static final int SELECT = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_edit_profile, null);
        act = this;

        binding.flBack.setOnClickListener(this);
        binding.tvEdit.setOnClickListener(this);
        binding.tvRecentWork01.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fl_back:
                finish();
                break;

            case R.id.tv_edit:

                break;

            case R.id.tv_recent_work_01:
                Intent intent = new Intent(act, SelectDlg.class);
                intent.putExtra("type", AppPreference.getProfilePref(act, AppPreference.PREF_GENDER).equalsIgnoreCase("male") ? "Male" : "Female");
                intent.putExtra("data", binding.tvRecentWork01.getText().toString());
                startActivityForResult(intent, SELECT);

                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String value = data.getStringExtra("value");

        switch (requestCode) {
            case SELECT:
                binding.tvRecentWork01.setText(value);
                break;
        }
    }
}
