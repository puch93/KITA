package kr.co.core.kita.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import org.json.JSONException;
import org.json.JSONObject;

import kr.co.core.kita.R;
import kr.co.core.kita.databinding.ActivityTermBinding;
import kr.co.core.kita.server.ReqBasic;
import kr.co.core.kita.server.netUtil.HttpResult;
import kr.co.core.kita.server.netUtil.NetUrls;
import kr.co.core.kita.util.Common;
import kr.co.core.kita.util.StringUtil;

public class TermAct extends BaseAct {
    ActivityTermBinding binding;
    Activity act;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_term, null);
        act = this;

        binding.flBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        getTerms();
    }

    private void getTerms() {
        ReqBasic server = new ReqBasic(act, NetUrls.TERMS) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {
                if (resultData.getResult() != null) {
                    try {
                        JSONObject jo = new JSONObject(resultData.getResult());

                        StringUtil.logLargeString(jo.toString());
                        String app_term_condition = StringUtil.getStr(jo, "app_customer_notice");

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                binding.tvTemrs.setText(app_term_condition);
                            }
                        });

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Common.showToastNetwork(act);
                    }
                } else {
                    Common.showToastNetwork(act);
                }
            }
        };

        server.setTag("Terms");
        server.execute(true, false);
    }
}
