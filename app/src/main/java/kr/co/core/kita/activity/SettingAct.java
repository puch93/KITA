package kr.co.core.kita.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import org.json.JSONException;
import org.json.JSONObject;

import kr.co.core.kita.R;
import kr.co.core.kita.databinding.ActivitySettingBinding;
import kr.co.core.kita.server.ReqBasic;
import kr.co.core.kita.server.netUtil.HttpResult;
import kr.co.core.kita.server.netUtil.NetUrls;
import kr.co.core.kita.util.AppPreference;
import kr.co.core.kita.util.Common;
import kr.co.core.kita.util.StringUtil;

public class SettingAct extends BaseAct implements View.OnClickListener {
    ActivitySettingBinding binding;
    Activity act;

    private static final int BILLING = 1001;

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
        binding.llLogout.setOnClickListener(this);
    }

    private void doLogout() {
        ReqBasic server = new ReqBasic(act, NetUrls.SET_OFFLINE) {
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

        server.setTag("Set Offline");
        server.addParams("m_idx", AppPreference.getProfilePref(act, AppPreference.PREF_MIDX));
        server.execute(true, false);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_payment:
                startActivityForResult(new Intent(act, PaymentAct.class), BILLING);
                break;

            case R.id.fl_back:
                finish();
                break;

            case R.id.ll_edit_profile:
                startActivity(new Intent(act, EditProfileAct.class));
                break;

            case R.id.ll_term:
//                startActivity(new Intent(act, TermSettingAct.class));
                startActivity(new Intent(act, TermAct.class));
//                Common.showToastDevelop(act);
                break;

            case R.id.ll_logout:
                showAlert(act, "Logout", "Would you like to logout?", new OnAfterConnection() {
                    @Override
                    public void onAfter() {
                        doLogout();
                        AppPreference.setProfilePrefBool(act, AppPreference.PREF_AUTO_LOGIN_STATE, false);
                        startActivity(new Intent(act, LoginAct.class));
                        finishAffinity();
                    }
                });
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case BILLING:
                    setResult(RESULT_OK);
                    break;
            }
        }
    }
}
