package kr.co.core.kita.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import androidx.databinding.DataBindingUtil;

import kr.co.core.kita.R;
import kr.co.core.kita.databinding.ActivityPaymentBinding;
import kr.co.core.kita.util.BillingEntireManager;
import kr.co.core.kita.util.Common;
import kr.co.core.kita.util.CustomApplication;

public class PaymentAct extends BaseAct implements View.OnClickListener {
    ActivityPaymentBinding binding;
    Activity act;

    private View selectedView;
    BillingEntireManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_payment, null);
        act = this;

        CustomApplication application = (CustomApplication) getApplication();
        manager = application.getManagerObject();


        binding.flBack.setOnClickListener(this);
        binding.llTicket.setOnClickListener(this);
        binding.llItem01.setOnClickListener(this);
        binding.llItem02.setOnClickListener(this);
        binding.llItem03.setOnClickListener(this);
        binding.tvPayment.setOnClickListener(this);

        binding.llItem01.setTag(R.string.pay_item_code, Common.ITEM_01_CODE);
        binding.llItem01.setTag(R.string.pay_item_name, Common.ITEM_01_NAME);
        binding.llItem01.setTag(R.string.pay_item_price, Common.ITEM_01_PRICE);

        binding.llItem02.setTag(R.string.pay_item_code, Common.ITEM_02_CODE);
        binding.llItem02.setTag(R.string.pay_item_name, Common.ITEM_02_NAME);
        binding.llItem02.setTag(R.string.pay_item_price, Common.ITEM_02_PRICE);

        binding.llItem03.setTag(R.string.pay_item_code, Common.ITEM_03_CODE);
        binding.llItem03.setTag(R.string.pay_item_name, Common.ITEM_03_NAME);
        binding.llItem03.setTag(R.string.pay_item_price, Common.ITEM_03_PRICE);

        binding.llItem01.performClick();
    }


    private void menuSelect(View selected) {
        binding.llItem01.setSelected(false);
        binding.llItem02.setSelected(false);
        binding.llItem03.setSelected(false);

        selected.setSelected(true);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fl_back:
                finish();
                break;

            case R.id.ll_ticket:
                break;

            case R.id.ll_item01:
            case R.id.ll_item02:
            case R.id.ll_item03:
                selectedView = v;
                menuSelect(v);
                break;

            case R.id.tv_payment:
                if (selectedView == null) {
                    Common.showToast(act, "결제할 아이템을 선택해주세요");
                } else {
                    if (manager.getManager_state().equals("N")) {
                        Common.showToast(act, manager.getManager_state_message());
                    } else if (manager.getInapp_state().equalsIgnoreCase("pending")) {
                        Common.showToast(act, "카드사 승인중인 결제가 있습니다. 몇분 후 앱을 재실행하여 결제가 정상적으로 진행되었는지 확인해주시기 바랍니다.");
                    } else {
                        manager.purchase((String) selectedView.getTag(R.string.pay_item_code), act);
                    }
                }
                break;
        }
    }
}
