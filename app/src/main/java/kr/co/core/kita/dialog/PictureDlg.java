package kr.co.core.kita.dialog;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import kr.co.core.kita.R;


public class PictureDlg extends BaseDlg {
    public static final String CAMERA = "camera";
    public static final String GALLERY = "gallery";
    Activity act;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        act = this;
        setContentView(R.layout.dialog_picture);

        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        getWindow().setAttributes(params);

//        setFinishOnTouchOutside(false);

        (findViewById(R.id.ll_camera)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(RESULT_OK, new Intent().putExtra("type", CAMERA));
                finish();
            }
        });

        (findViewById(R.id.ll_gallery)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(RESULT_OK, new Intent().putExtra("type", GALLERY));
                finish();
            }
        });
    }
}
