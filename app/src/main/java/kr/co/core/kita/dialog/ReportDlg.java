package kr.co.core.kita.dialog;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import org.json.JSONException;
import org.json.JSONObject;

import kr.co.core.kita.R;
import kr.co.core.kita.server.ReqBasic;
import kr.co.core.kita.server.netUtil.HttpResult;
import kr.co.core.kita.server.netUtil.NetUrls;
import kr.co.core.kita.util.AppPreference;
import kr.co.core.kita.util.Common;
import kr.co.core.kita.util.StringUtil;


public class ReportDlg extends BaseDlg {
    Activity act;

    private String yidx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        act = this;

        yidx = getIntent().getStringExtra("yidx");

        if (StringUtil.isNull(yidx)) {
            finish();
        }

        setContentView(R.layout.dialog_report);

        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        getWindow().setAttributes(params);

        setFinishOnTouchOutside(false);

        (findViewById(R.id.tv_left)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        (findViewById(R.id.tv_right)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doReport();
            }
        });
    }

    private void doReport() {
        ReqBasic server = new ReqBasic(act, NetUrls.REPORT) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {
                if (resultData.getResult() != null) {
                    try {
                        JSONObject jo = new JSONObject(resultData.getResult());

                        if (StringUtil.getStr(jo, "result").equalsIgnoreCase("Y") || StringUtil.getStr(jo, "result").equalsIgnoreCase(NetUrls.SUCCESS)) {
                            Common.showToast(act, "Reported successfully");
                            finish();
                        } else {
                            Common.showToast(act, "Already reported");
                            Log.i(StringUtil.TAG, "value: " + StringUtil.getStr(jo, "value"));
                            finish();
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

        server.setTag("Report");
        server.addParams("midx", AppPreference.getProfilePref(act, AppPreference.PREF_MIDX));
        server.addParams("yidx", yidx);
        server.execute(true, false);
    }
}
