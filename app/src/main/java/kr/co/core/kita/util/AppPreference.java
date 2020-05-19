package kr.co.core.kita.util;

import android.content.Context;
import android.content.SharedPreferences;

public class AppPreference {
    /* profile string key */
    public static final String PREF_MIDX = "midx";
    public static final String PREF_FCM = "fcm";
    public static final String PREF_ID = "id";
    public static final String PREF_PW = "pw";
    public static final String PREF_GENDER = "gender";
    public static final String PREF_SEARCH = "search";

    // profile string
    public static void setProfilePref(Context context, String key, String value) {
        SharedPreferences pref = context.getSharedPreferences("profile", context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(key, value);
        editor.commit();
    }
    public static String getProfilePref(Context context, String key) {
        SharedPreferences pref = context.getSharedPreferences("profile", context.MODE_PRIVATE);
        return pref.getString(key, null);
    }

}
