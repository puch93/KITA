package kr.co.core.kita.dialog;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import kr.co.core.kita.R;
import kr.co.core.kita.util.StringUtil;


public class WarningPopupDlg extends BaseDlg {
    public static final String REPORT = "report";
    Activity act;

    private String type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        act = this;

        type = getIntent().getStringExtra("type");

        if (StringUtil.isNull(type)) {
            finish();
        }

        switch (type) {
            case REPORT:
                setContentView(R.layout.dialog_report);
                break;
        }

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
                finish();
            }
        });
    }
}
