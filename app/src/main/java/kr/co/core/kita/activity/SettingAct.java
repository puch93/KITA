package kr.co.core.kita.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import kr.co.core.kita.R;
import kr.co.core.kita.databinding.ActivitySettingBinding;

public class SettingAct extends BaseAct implements View.OnClickListener {
    ActivitySettingBinding binding;
    Activity act;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_setting, null);
        act = this;

        binding.flBack.setOnClickListener(this);
        binding.llEditProfile.setOnClickListener(this);
        binding.llTerm.setOnClickListener(this);
        binding.llLogout.setOnClickListener(this);
        binding.llPayment.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_payment:
                startActivity(new Intent(act, PaymentAct.class));
                break;

            case R.id.fl_back:
                finish();
                break;

            case R.id.ll_edit_profile:
                startActivity(new Intent(act, EditProfileAct.class));
                break;

            case R.id.ll_term:
                startActivity(new Intent(act, TermSettingAct.class));
                break;

            case R.id.ll_logout:
                break;
        }
    }
}
