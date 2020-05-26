package kr.co.core.kita.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import kr.co.core.kita.R;
import kr.co.core.kita.databinding.ActivityTermJoinBinding;

public class TermJoinAct extends AppCompatActivity {
    ActivityTermJoinBinding binding;
    Activity act;

    String join_type = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_term_join, null);
        act = this;

        join_type = getIntent().getStringExtra("join_type");

        binding.tvDisagree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        binding.tvAgree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(act, JoinAct.class).putExtra("join_type", join_type));
                finish();
            }
        });
    }
}
