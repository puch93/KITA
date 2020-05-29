package kr.co.core.kita.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import androidx.databinding.DataBindingUtil;

import org.json.JSONException;
import org.json.JSONObject;

import kr.co.core.kita.R;
import kr.co.core.kita.databinding.ActivityGiftBinding;
import kr.co.core.kita.server.ReqBasic;
import kr.co.core.kita.server.netUtil.HttpResult;
import kr.co.core.kita.server.netUtil.NetUrls;
import kr.co.core.kita.util.AppPreference;
import kr.co.core.kita.util.Common;
import kr.co.core.kita.util.StringUtil;

public class GiftAct extends BaseAct implements View.OnClickListener {
    ActivityGiftBinding binding;
    Activity act;

    String selectedName = "";
    String selectedPrice = "";
    String yidx = "", nick = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_gift, null);
        act = this;

        yidx = getIntent().getStringExtra("yidx");
        nick = getIntent().getStringExtra("nick");

        binding.flBack.setOnClickListener(this);
        binding.llItem01.setOnClickListener(this);
        binding.llItem02.setOnClickListener(this);
        binding.llItem03.setOnClickListener(this);
        binding.llItem04.setOnClickListener(this);
        binding.llItem05.setOnClickListener(this);
        binding.llItem06.setOnClickListener(this);
        binding.llItem07.setOnClickListener(this);

        binding.llItem01.setTag(R.string.gift_name, "Heart");
        binding.llItem02.setTag(R.string.gift_name, "Jollibee");
        binding.llItem03.setTag(R.string.gift_name, "Flower basket");
        binding.llItem04.setTag(R.string.gift_name, "Tequila");
        binding.llItem05.setTag(R.string.gift_name, "Weak price");
        binding.llItem06.setTag(R.string.gift_name, "Condolence");
        binding.llItem07.setTag(R.string.gift_name, "1 day from tomorrow");

        binding.llItem01.setTag(R.string.gift_price, "10");
        binding.llItem02.setTag(R.string.gift_price, "180");
        binding.llItem03.setTag(R.string.gift_price, "100");
        binding.llItem04.setTag(R.string.gift_price, "350");
        binding.llItem05.setTag(R.string.gift_price, "500");
        binding.llItem06.setTag(R.string.gift_price, "1000");
        binding.llItem07.setTag(R.string.gift_price, "10000");
    }

    private void doGift() {
        ReqBasic server = new ReqBasic(act, NetUrls.GIFT) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {
                if (resultData.getResult() != null) {
                    try {
                        JSONObject jo = new JSONObject(resultData.getResult());

                        if (StringUtil.getStr(jo, "result").equalsIgnoreCase("Y") || StringUtil.getStr(jo, "result").equalsIgnoreCase(NetUrls.SUCCESS)) {
                            Common.showToast(act, "Presented successfully");
                            finish();
                        } else {
                            Common.showToast(act, "Your PHP is not enough");
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

        server.setTag("Gift");
        server.addParams("m_idx", AppPreference.getProfilePref(act, AppPreference.PREF_MIDX));
        server.addParams("y_idx", yidx);
        server.addParams("g_name", selectedName);
        server.addParams("g_price", selectedPrice);
        server.execute(true, false);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fl_back:
                finish();
                break;

            case R.id.ll_item_01:
            case R.id.ll_item_02:
            case R.id.ll_item_03:
            case R.id.ll_item_04:
            case R.id.ll_item_05:
            case R.id.ll_item_06:
            case R.id.ll_item_07:
                selectedName = (String) v.getTag(R.string.gift_name);
                selectedPrice = (String) v.getTag(R.string.gift_price);

                showAlert(act, selectedName, "Would you like to present it to a " + nick + "?", new OnAfterConnection() {
                    @Override
                    public void onAfter() {
                        doGift();
                    }
                });
                break;
        }
    }
}
