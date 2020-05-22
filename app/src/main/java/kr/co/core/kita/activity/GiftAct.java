package kr.co.core.kita.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;
import android.view.View;

import kr.co.core.kita.R;
import kr.co.core.kita.databinding.ActivityGiftBinding;

public class GiftAct extends BaseAct implements View.OnClickListener {
    ActivityGiftBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_gift, null);

        binding.flBack.setOnClickListener(this);
        binding.llItem01.setOnClickListener(this);
        binding.llItem02.setOnClickListener(this);
        binding.llItem03.setOnClickListener(this);
        binding.llItem04.setOnClickListener(this);
        binding.llItem05.setOnClickListener(this);
        binding.llItem06.setOnClickListener(this);
        binding.llItem07.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fl_back:
                finish();
                break;
        }
    }
}
