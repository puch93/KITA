package kr.co.core.kita.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import java.util.ArrayList;

import kr.co.core.kita.R;
import kr.co.core.kita.adapter.HistoryCallAdapter;
import kr.co.core.kita.data.HistoryCallData;
import kr.co.core.kita.databinding.ActivityCallHistoryBinding;
import kr.co.core.kita.util.AllOfDecoration;
import kr.co.core.kita.util.AppPreference;

public class CallHistoryAct extends AppCompatActivity {
    ActivityCallHistoryBinding binding;
    Activity act;

    HistoryCallAdapter adapter;
    ArrayList<HistoryCallData> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_call_history, null);
        act = this;

        AppPreference.setProfilePref(act, AppPreference.PREF_SEARCH, null);

        binding.recyclerView.setLayoutManager(new GridLayoutManager(act, 2));
        binding.recyclerView.setHasFixedSize(true);
        binding.recyclerView.setItemViewCacheSize(20);
        adapter = new HistoryCallAdapter(act, list);
        binding.recyclerView.setAdapter(adapter);
        binding.recyclerView.addItemDecoration(new AllOfDecoration(act, AllOfDecoration.HISTORY_CALL_LIST));

        setTestData();

        binding.flBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void setTestData() {
        for (int i = 0; i < 10; i++) {
            list.add(new HistoryCallData(null, null, "마동석", "Philippines", i % 2 == 0, "5:30"));
        }

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.setList(list);
            }
        });
    }
}
