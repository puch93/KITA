package kr.co.core.kita.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;
import android.view.View;

import kr.co.core.kita.R;
import kr.co.core.kita.databinding.ActivityPaymentBinding;
import kr.co.core.kita.util.Common;

public class PaymentAct extends BaseAct implements View.OnClickListener {
    ActivityPaymentBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_payment, null);

        binding.flBack.setOnClickListener(this);
        binding.llTicket.setOnClickListener(this);
        binding.llItem01.setOnClickListener(this);
        binding.llItem02.setOnClickListener(this);
        binding.llItem03.setOnClickListener(this);
        binding.tvPayment.setOnClickListener(this);

        binding.llItem01.setTag(Common.ITEM_01);
        binding.llItem02.setTag(Common.ITEM_02);
        binding.llItem03.setTag(Common.ITEM_03);
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
                break;
        }
    }
}
