package kr.co.core.kita.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import java.util.ArrayList;

import kr.co.core.kita.R;
import kr.co.core.kita.adapter.ProfileTalkAdapter;
import kr.co.core.kita.data.ProfileTalkData;
import kr.co.core.kita.databinding.ActivityProfileDetailBinding;
import kr.co.core.kita.dialog.WarningPopupDlg;
import kr.co.core.kita.util.AllOfDecoration;

public class ProfileDetailAct extends BaseAct implements View.OnClickListener {
    ActivityProfileDetailBinding binding;
    Activity act;

    ProfileTalkAdapter adapter;
    ArrayList<ProfileTalkData> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_profile_detail, null);
        act = this;

        binding.flBack.setOnClickListener(this);
        binding.flReport.setOnClickListener(this);


        binding.recyclerView.setLayoutManager(new GridLayoutManager(act, 3));
        binding.recyclerView.setItemViewCacheSize(20);
        binding.recyclerView.setHasFixedSize(true);
        adapter = new ProfileTalkAdapter(act, list);
        binding.recyclerView.setAdapter(adapter);
        binding.recyclerView.addItemDecoration(new AllOfDecoration(act, AllOfDecoration.PROFILE_DETAIL));


        setTestData();
    }

    private void setTestData() {
        for (int i = 0; i < 20; i++) {
            list.add(new ProfileTalkData(null, null));
        }

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
            case R.id.fl_report:
                startActivity(new Intent(act, WarningPopupDlg.class).putExtra("type", WarningPopupDlg.REPORT));
                break;
            case R.id.fl_back:
                finish();
                break;
        }
    }
}
