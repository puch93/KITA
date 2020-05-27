package kr.co.core.kita.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class BaseAct extends AppCompatActivity {

    public interface OnAfterConnection {
        void onAfter();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    public void showAlert(Activity act, String title, String contents, OnAfterConnection onAfterConnection) {
        androidx.appcompat.app.AlertDialog.Builder alertDialog = new androidx.appcompat.app.AlertDialog.Builder(act);

        alertDialog.setCancelable(false);
        alertDialog.setTitle(title);
        alertDialog.setMessage(contents);

        // ok
        alertDialog.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        onAfterConnection.onAfter();
                        dialog.cancel();
                    }
                });
        // cancel
        alertDialog.setNegativeButton("CANCEL",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        alertDialog.show();
    }
}
