package kr.co.core.kita.util;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class StringUtil {
    public static final String TAG = "TEST_HOME";
    public static final String TAG_PUSH = "TEST_PUSH";
    public static final String TAG_PAY = "TEST_PAY";
    public static final String TAG_CHAT = "TEST_CHAT";

    public static boolean isNull(String str) {
        if (str == null || str.length() == 0 || str.equals("null")) {
            return true;
        } else {
            return false;
        }
    }

    public static String converTime(String original, String pattern) {
        //아이템별 시간
        SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", java.util.Locale.getDefault());
        Date date1 = null;
        try {
            date1 = dateFormat1.parse(original);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        SimpleDateFormat dateFormat2 = new SimpleDateFormat(pattern, java.util.Locale.getDefault());
        return dateFormat2.format(date1);
    }

    public static String setNumComma(int price) {
        DecimalFormat format = new DecimalFormat("###,###");
        return format.format(price);
    }

    public static String calcAge(String byear) {
        // 현재 연도에서 출생 연도를 뺀다. (2018 - 2000 = 18)
        // 1살을 더한다. (18 + 1 = 19)
        Calendar c = Calendar.getInstance();
//        Log.i(TAG,"year: "+(c.get(Calendar.YEAR)-Integer.parseInt(byear)+1));
        int lastYear = c.get(Calendar.YEAR) - Integer.parseInt(byear) + 1;

        return String.valueOf(lastYear);
    }

    public static String getStr(JSONObject jo, String key) {
        String s = null;
        try {
            if (jo.has(key)) {
                s = jo.getString(key);
            } else {
                s = "";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return s;
    }

    // 안드로이드10 부터 디바이스id 가져오는거 안되서 다른방법 사용
    public static String getDeviceId(Context ctx) {
        String newId = "";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            newId = "35" +
                    Build.BOARD.length() % 10 + Build.BRAND.length() % 10 +
                    Build.DEVICE.length() % 10 +
                    Build.DISPLAY.length() % 10 + Build.HOST.length() % 10 +
                    Build.ID.length() % 10 + Build.MANUFACTURER.length() % 10 +
                    Build.MODEL.length() % 10 + Build.PRODUCT.length() % 10 +
                    Build.TAGS.length() % 10 + Build.TYPE.length() % 10 +
                    Build.USER.length() % 10;
        } else {
            if (ActivityCompat.checkSelfPermission(ctx, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
                newId = ((TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
            }

            if (StringUtil.isNull(newId)) {
                newId = "35" +
                        Build.BOARD.length() % 10 + Build.BRAND.length() % 10 +
                        Build.CPU_ABI.length() % 10 + Build.DEVICE.length() % 10 +
                        Build.DISPLAY.length() % 10 + Build.HOST.length() % 10 +
                        Build.ID.length() % 10 + Build.MANUFACTURER.length() % 10 +
                        Build.MODEL.length() % 10 + Build.PRODUCT.length() % 10 +
                        Build.TAGS.length() % 10 + Build.TYPE.length() % 10 +
                        Build.USER.length() % 10;
            }
        }

        if (!StringUtil.isNull(newId)) {
//            AppPreference.setProfilePref(ctx, AppPreference.PREF_DEVICE_ID, newId);
            Log.e(StringUtil.TAG, "device id: " + newId);
        }

        return newId;
    }

    public static void logLargeString(String str) {
        if (str.length() > 1500) {
            Log.i(StringUtil.TAG, str.substring(0, 1500));
            logLargeString(str.substring(1500));
        } else {
            Log.i(StringUtil.TAG, str); // continuation
        }
    }
}
