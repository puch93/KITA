package kr.co.core.kita.dialog;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import kr.co.core.kita.R;
import kr.co.core.kita.activity.BaseAct;
import kr.co.core.kita.activity.GiftHistoryAct;

public class GiftDlg extends BaseAct {
    Activity act;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        act = this;

        String nick = getIntent().getStringExtra("nick");
        String gift_name = getIntent().getStringExtra("gift_name");

        setContentView(R.layout.dialog_gift);

        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        getWindow().setAttributes(params);

        setFinishOnTouchOutside(false);

        ((TextView) findViewById(R.id.tv_nick)).setText(nick);
        ((TextView) findViewById(R.id.tv_gift_name)).setText(gift_name);

        (findViewById(R.id.tv_left)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        (findViewById(R.id.tv_right)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(act, GiftHistoryAct.class));
                finish();
            }
        });
    }
}
