package kr.co.core.kita.activity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.databinding.DataBindingUtil;

import org.json.JSONException;
import org.json.JSONObject;

import kr.co.core.kita.R;
import kr.co.core.kita.databinding.ActivityPaymentBinding;
import kr.co.core.kita.server.ReqBasic;
import kr.co.core.kita.server.netUtil.HttpResult;
import kr.co.core.kita.server.netUtil.NetUrls;
import kr.co.core.kita.util.AppPreference;
import kr.co.core.kita.util.BillingEntireManager;
import kr.co.core.kita.util.Common;
import kr.co.core.kita.util.CustomApplication;
import kr.co.core.kita.util.StringUtil;

public class PaymentAct extends BaseAct implements View.OnClickListener {
    ActivityPaymentBinding binding;
    public static Activity act;

    private View selectedView;
    BillingEntireManager manager;
//    private boolean isAutoPay = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_payment, null);
        act = this;

        CustomApplication application = (CustomApplication) getApplication();
        manager = application.getManagerObject();

//        checkAutoPay();

        binding.flBack.setOnClickListener(this);
        binding.llTicket.setOnClickListener(this);
        binding.llItem01.setOnClickListener(this);
        binding.llItem02.setOnClickListener(this);
        binding.llItem03.setOnClickListener(this);
        binding.tvGoogle.setOnClickListener(this);
//        binding.tvZeropay.setOnClickListener(this);
        binding.tvTicket.setOnClickListener(this);
//        binding.tvSubs.setOnClickListener(this);

        binding.llItem01.setTag(R.string.pay_item_code, Common.ITEM_01_CODE);
        binding.llItem01.setTag(R.string.pay_item_name, Common.ITEM_01_NAME);
        binding.llItem01.setTag(R.string.pay_item_price, Common.ITEM_01_PRICE);

        binding.llItem02.setTag(R.string.pay_item_code, Common.ITEM_02_CODE);
        binding.llItem02.setTag(R.string.pay_item_name, Common.ITEM_02_NAME);
        binding.llItem02.setTag(R.string.pay_item_price, Common.ITEM_02_PRICE);

        binding.llItem03.setTag(R.string.pay_item_code, Common.ITEM_03_CODE);
        binding.llItem03.setTag(R.string.pay_item_name, Common.ITEM_03_NAME);
        binding.llItem03.setTag(R.string.pay_item_price, Common.ITEM_03_PRICE);

        binding.llTicket.setTag(R.string.pay_item_code, Common.SUBS_CHAT_CODE);
        binding.llTicket.setTag(R.string.pay_item_name, Common.SUBS_CHAT_NAME);
        binding.llTicket.setTag(R.string.pay_item_price, Common.SUBS_CHAT_PRICE);

        binding.llItem01.performClick();
    }

//    // 구독중인지 체크
//    private void checkAutoPay() {
//        ReqBasic server = new ReqBasic(act, NetUrls.CHECK_AUTOPAY) {
//            @Override
//            public void onAfter(int resultCode, HttpResult resultData) {
//                if (resultData.getResult() != null) {
//                    try {
//                        JSONObject jo = new JSONObject(resultData.getResult());
//
//                        if (StringUtil.getStr(jo, "result").equalsIgnoreCase("Y") || StringUtil.getStr(jo, "result").equalsIgnoreCase(NetUrls.SUCCESS)) {
//                            //구독중
//                            isAutoPay = true;
//                        } else {
//                            //구독중 아님
//                            isAutoPay = false;
//                        }
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                        Common.showToastNetwork(act);
//                    }
//                } else {
//                    Common.showToastNetwork(act);
//                }
//            }
//        };
//
//        server.setTag("Check Auto Pay");
//        server.addParams("m_idx", AppPreference.getProfilePref(act, AppPreference.PREF_MIDX));
//        server.execute(true, false);
//    }


    private void menuSelect(View selected) {
        binding.llTicket.setSelected(false);
        binding.llItem01.setSelected(false);
        binding.llItem02.setSelected(false);
        binding.llItem03.setSelected(false);

        selected.setSelected(true);
    }

//    private void checkChattingTicket() {
//        ReqBasic server = new ReqBasic(act, NetUrls.CHAT_TICKET_PURCHASED) {
//            @Override
//            public void onAfter(int resultCode, HttpResult resultData) {
//                if (resultData.getResult() != null) {
//                    try {
//                        JSONObject jo = new JSONObject(resultData.getResult());
//
//                        if (StringUtil.getStr(jo, "result").equalsIgnoreCase("Y") || StringUtil.getStr(jo, "result").equalsIgnoreCase(NetUrls.SUCCESS)) {
//                            Common.showToast(act, "Already in use. Please repurchase after the expiration date.");
//                        } else {
//                            doBuyTicket();
//                        }
//
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                        Common.showToastNetwork(act);
//                    }
//                } else {
//                    Common.showToastNetwork(act);
//                }
//            }
//        };
//
//        server.setTag("Check Chatting Ticket");
//        server.addParams("m_idx", AppPreference.getProfilePref(act, AppPreference.PREF_MIDX));
//        server.execute(true, false);
//    }

//    private void cancelAutoPay() {
//        ReqBasic server = new ReqBasic(act, NetUrls.CANCEL_AUTOPAY) {
//            @Override
//            public void onAfter(int resultCode, HttpResult resultData) {
//                if (resultData.getResult() != null) {
//                    try {
//                        JSONObject jo = new JSONObject(resultData.getResult());
//
//                        if (StringUtil.getStr(jo, "result").equalsIgnoreCase("Y") || StringUtil.getStr(jo, "result").equalsIgnoreCase(NetUrls.SUCCESS)) {
//                            Common.showToast(act, "Canceled successfully.");
//                            isAutoPay = false;
//
//                            runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    binding.tvTicket.setVisibility(View.VISIBLE);
//                                    binding.tvSubs.setVisibility(View.GONE);
//                                }
//                            });
//
//                        } else {
//                            Common.showToastNetwork(act);
//                        }
//
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                        Common.showToastNetwork(act);
//                    }
//                } else {
//                    Common.showToastNetwork(act);
//                }
//            }
//        };
//
//        server.setTag("Cancel Auto Pay");
//        server.addParams("m_idx", AppPreference.getProfilePref(act, AppPreference.PREF_MIDX));
//        server.execute(true, false);
//    }

    private void doBuyTicket() {
        ReqBasic server = new ReqBasic(act, NetUrls.PAY_TICKET) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {
                if (resultData.getResult() != null) {
                    try {
                        JSONObject jo = new JSONObject(resultData.getResult());

                        if (StringUtil.getStr(jo, "result").equalsIgnoreCase("Y") || StringUtil.getStr(jo, "result").equalsIgnoreCase(NetUrls.SUCCESS)) {
                            Common.showToast(act, "Purchased successfully");
                            finish();
                        } else {
//                            Common.showToast(act, StringUtil.getStr(jo, "msg"));
                            Log.i(StringUtil.TAG, "msg: " + StringUtil.getStr(jo, "msg"));
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

        server.setTag("Pay Ticket");
        server.addParams("m_idx", AppPreference.getProfilePref(act, AppPreference.PREF_MIDX));
        server.execute(true, false);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fl_back:
                finish();
                break;

            case R.id.ll_ticket:
                selectedView = v;

                binding.llButtonArea.setVisibility(View.GONE);
                binding.tvTicket.setVisibility(View.VISIBLE);

                binding.llItem01.setSelected(false);
                binding.llItem02.setSelected(false);
                binding.llItem03.setSelected(false);

                binding.llTicket.setSelected(true);
//                if (isAutoPay) {
//                    binding.tvTicket.setVisibility(View.GONE);
//                    binding.tvSubs.setVisibility(View.VISIBLE);
//                } else {
//                    binding.tvTicket.setVisibility(View.VISIBLE);
//                    binding.tvSubs.setVisibility(View.GONE);
//                }
                break;

//            case R.id.tv_subs:
//                showAlert(act, "Cancel subscription", "Would you like to unsubscribe?", new OnAfterConnection() {
//                    @Override
//                    public void onAfter() {
//                        cancelAutoPay();
//                    }
//                });
//                break;


            case R.id.ll_item01:
            case R.id.ll_item02:
            case R.id.ll_item03:
                binding.tvTicket.setVisibility(View.GONE);
//                binding.tvSubs.setVisibility(View.GONE);
                binding.llButtonArea.setVisibility(View.VISIBLE);

                selectedView = v;
                menuSelect(v);
                break;

            case R.id.tv_google:
                if (selectedView == null) {
                    Common.showToast(act, "Please select an item to pay");
                } else {
                    if (manager.getManager_state().equals("N")) {
                        Common.showToast(act, manager.getManager_state_message());
                    } else if (manager.getInapp_state().equalsIgnoreCase("pending")) {
                        Common.showToast(act, "카드사 승인중인 결제가 있습니다. 몇분 후 앱을 재실행하여 결제가 정상적으로 진행되었는지 확인해주시기 바랍니다.");
                    } else {
                        manager.purchase((String) selectedView.getTag(R.string.pay_item_code), true, act);
                    }
                }
                break;
//            case R.id.tv_zeropay:
//                startActivity(new Intent(act, PaymentDlg.class)
//                        .putExtra("i_name", (String) selectedView.getTag(R.string.pay_item_name))
//                        .putExtra("i_code", (String) selectedView.getTag(R.string.pay_item_code))
//                        .putExtra("i_price", (String) selectedView.getTag(R.string.pay_item_price)));
//                break;

            case R.id.tv_ticket:
                if (selectedView == null) {
                    Common.showToast(act, "Please select an item to pay");
                } else {
                    if(AppPreference.getProfilePref(act, AppPreference.PREF_GENDER).equalsIgnoreCase(Common.GENDER_M)) {
                        if (manager.getManager_state().equals("N")) {
                            Common.showToast(act, manager.getManager_state_message());
                        } else if (manager.getSubscription_state().equalsIgnoreCase("pending")) {
                            Common.showToast(act, "카드사 승인중인 결제가 있습니다. 몇분 후 앱을 재실행하여 결제가 정상적으로 진행되었는지 확인해주시기 바랍니다.");
                        } else {
                            manager.purchase((String) selectedView.getTag(R.string.pay_item_code), false, act);
                        }
                    } else {
                        Common.showToast(act, "It cannot be purchased. Female members can chat for free.");
                    }
                }
                break;
        }
    }
}
