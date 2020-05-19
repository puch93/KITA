package kr.co.core.kita.activity;

import androidx.databinding.DataBindingUtil;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import kr.co.core.kita.R;
import kr.co.core.kita.databinding.ActivityLoginBinding;

public class LoginAct extends BaseAct implements View.OnClickListener {
    ActivityLoginBinding binding;
    Activity act;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login, null);
        act = this;

        binding.flBack.setOnClickListener(this);
        binding.llLoginBtn.setOnClickListener(this);
        binding.llNaverBtn.setOnClickListener(this);
        binding.llFacebookBtn.setOnClickListener(this);
        binding.tvJoin.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fl_back:
                finish();
                break;

            case R.id.ll_login_btn:
                startActivity(new Intent(act, MainAct.class));
                finish();
                break;
            case R.id.ll_naver_btn:
                break;
            case R.id.ll_facebook_btn:
                break;

            case R.id.tv_join:
                startActivity(new Intent(act, TermJoinAct.class));
                break;
        }
    }
}
