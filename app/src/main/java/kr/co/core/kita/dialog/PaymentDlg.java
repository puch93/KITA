package kr.co.core.kita.dialog;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.databinding.DataBindingUtil;

import com.android.billingclient.api.Purchase;

import org.json.JSONException;
import org.json.JSONObject;

import kr.co.core.kita.R;
import kr.co.core.kita.activity.PaymentAct;
import kr.co.core.kita.databinding.DialogPaymentBinding;
import kr.co.core.kita.server.ReqBasic;
import kr.co.core.kita.server.netUtil.HttpResult;
import kr.co.core.kita.server.netUtil.NetUrls;
import kr.co.core.kita.util.AppPreference;
import kr.co.core.kita.util.Common;
import kr.co.core.kita.util.StringUtil;

public class PaymentDlg extends BaseDlg {
    DialogPaymentBinding binding;
    Activity act;

    @Override
    protected void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        binding = DataBindingUtil.setContentView(this, R.layout.dialog_payment);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        act = this;

        setLayout();
    }

    private void setLayout() {
        binding.paymentWebview.setWebChromeClient(new WebChromeClient());
        binding.paymentWebview.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                binding.paymentWebview.loadUrl(request.getUrl().toString());
                return true;
            }
        });
        binding.paymentWebview.getSettings().setJavaScriptEnabled(true);
        binding.paymentWebview.getSettings().setAllowFileAccess(true);
        binding.paymentWebview.getSettings().setAllowContentAccess(true);
        binding.paymentWebview.getSettings().setDomStorageEnabled(true);
        binding.paymentWebview.addJavascriptInterface(new AndroidBridge(), "AndroidInterface");


        String i_name = getIntent().getStringExtra("i_name");
        String i_code = getIntent().getStringExtra("i_code");
        String i_price = getIntent().getStringExtra("i_price");


        String complete = "window.AndroidInterface.showToast(\"결제완료 되었습니다. 감사합니다.\")";


        String resulturl = NetUrls.DOMAIN + "/payment/result.siso";

        String post = "siteUrl=" + NetUrls.DOMAIN +
                "&CONNECTCODE=APP" +
                "&_APP_MEM_IDX=" + AppPreference.getProfilePref(act, AppPreference.PREF_MIDX) +
                "&PRODUCTNAME=" + i_name +
                "&PRICE=" + i_price +
                "&ZEROEXTEND1=" + i_code +
                "&ZEROEXTEND2=kita" +
                "&returnUrl=" + Base64.encodeToString(resulturl.getBytes(), 0) +
                "&complate_script=" + Base64.encodeToString(complete.getBytes(), 0) +
                "&ID=" + AppPreference.getProfilePref(act, AppPreference.PREF_ID) +
                "&CODE=" + AppPreference.getProfilePref(act, AppPreference.PREF_MIDX) +
                "&NAME=" +
                "&EMAIL=" +
                "&TEL=";

//        String post = "siteUrl=" + NetUrls.DOMAIN +
//                "&CONNECTCODE=APP" +
//                "&_APP_MEM_IDX=" + AppPreference.getProfilePref(act, AppPreference.PREF_MIDX) +
//                "&dbControl=getBlockSenderListCk" +
//                "&PRODUCTNAME=" + i_name +
//                "&PRICE=" + (Integer.valueOf(i_price) * 1.1) +
////                "&ZEROEXTEND1=" + getIntent().getStringExtra("key") + "|||||" + getIntent().getStringExtra("cnt") +
//                "&ZEROEXTEND1=" + "test" +
//                "&ZEROEXTEND2=" + "kita" +
//                "&returnUrl=" + Base64.encodeToString(resulturl.getBytes(), 0) +
//                "&complate_script=" + Base64.encodeToString(complete.getBytes(), 0) +
//                "&CPID=CCP21773" +
//                "&LITEIS=N" +
//                "&ID=" + AppPreference.getProfilePref(act, AppPreference.PREF_ID) +
//                "&CODE=" + AppPreference.getProfilePref(act, AppPreference.PREF_MIDX) +
//                "&NAME=" + AppPreference.getProfilePref(act, AppPreference.PREF_ID) + //TODO
//                "&EMAIL=" +
//                "&TEL=" +
//                "&p_itemtype=" +
//                "&p_itemcount=" +
//                "&item_class=" +
//                "&item_name=";

        System.out.println("post : " + post);

        binding.paymentWebview.postUrl(NetUrls.DOMAIN + "/payment/card.paymnet.siso", post.getBytes());

        //getIntent().getStringExtra("price").replaceAll("," , "")

//        sendPurchaseResult();
    }


    protected static String getTel(Context contenxt) {
        TelephonyManager tm = (TelephonyManager) contenxt.getSystemService(contenxt.TELEPHONY_SERVICE);
        String realTel = tm.getLine1Number();
        if (realTel != null) {
            if (realTel.indexOf("+82") == 0) {
                realTel = "0" + realTel.substring(3, realTel.length());
            }
        } else
            realTel = "00000000000";
        return realTel;
    }

    private class AndroidBridge {
        @JavascriptInterface
        public void showToast(final String toast) {
            System.out.println("check toast point!!!!!!!");
            System.out.println("toast: " + toast);
            finish();
        }
    }

    private void sendPurchaseResult() {
        ReqBasic server = new ReqBasic(act, NetUrls.PAY_RESULT) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {
                if (resultData.getResult() != null) {
                    try {
                        JSONObject jo = new JSONObject(resultData.getResult());

                        if (StringUtil.getStr(jo, "result").equalsIgnoreCase(NetUrls.SUCCESS) || StringUtil.getStr(jo, "result").equalsIgnoreCase("Y")) {
                            Common.showToast(act, "Payment for recent order has been completed.");
//                            afterListener.sendMessage("최근주문건에 대한 결제 완료되었습니다", true);
                            if (PaymentAct.act != null) {
                                PaymentAct.act.setResult(Activity.RESULT_OK);
                                PaymentAct.act.finish();
                            }
                        } else {

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {

                }
            }
        };

        String name;
        String price;


        name = Common.ITEM_01_NAME;
        price = Common.ITEM_01_PRICE;


        server.setTag("Pay Result");

        server.addParams("midx", AppPreference.getProfilePref(act, AppPreference.PREF_MIDX));
        server.addParams("itype", "point");
        server.addParams("isubject", name);
        server.addParams("icode", Common.ITEM_01_CODE);

        server.addParams("p_order_id", Common.ITEM_01_CODE);
        server.addParams("p_store_type", "zeropay");
        server.addParams("p_purchase_time", StringUtil.convertCallTime(System.currentTimeMillis(), "yyyy-MM-dd hh:mm:ss"));
        server.addParams("p_purchase_price", price);
        server.addParams("p_signature", "none");
        server.addParams("p_info", "none");

        server.execute(true, false);
    }
}
