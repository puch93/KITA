package kr.co.core.kita.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import kr.co.core.kita.R;
import kr.co.core.kita.databinding.ActivitySearchBinding;
import kr.co.core.kita.util.Common;

public class SearchAct extends AppCompatActivity {
    ActivitySearchBinding binding;
    Activity act;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_search, null);
        act = this;

        binding.etContents.setText(getIntent().getStringExtra("data"));

        binding.flBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        binding.flSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_OK, new Intent().putExtra("value", binding.etContents.getText().toString()));
                finish();
            }
        });
    }
}
