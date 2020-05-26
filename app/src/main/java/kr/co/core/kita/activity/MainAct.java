package kr.co.core.kita.activity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import kr.co.core.kita.R;
import kr.co.core.kita.databinding.ActivityMainBinding;
import kr.co.core.kita.fragment.BaseFrag;
import kr.co.core.kita.fragment.ChatFrag;
import kr.co.core.kita.fragment.HomeFrag;
import kr.co.core.kita.fragment.MeFrag;
import kr.co.core.kita.fragment.TalkFrag;
import kr.co.core.kita.util.BackPressCloseHandler;
import kr.co.core.kita.util.StringUtil;

public class MainAct extends BaseAct implements View.OnClickListener {
    ActivityMainBinding binding;
    Activity act;
    FragmentManager fragmentManager;

    private BackPressCloseHandler backPressCloseHandler;

    HomeFrag homeFrag = null;
    TalkFrag talkFrag = null;
    ChatFrag chatFrag = null;
    MeFrag meFrag = null;

    public static final String TAG_HOME = "home";
    public static final String TAG_TALK = "talk";
    public static final String TAG_CHAT = "chat";
    public static final String TAG_ME = "me";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main, null);
        act = this;


        backPressCloseHandler = new BackPressCloseHandler(this);

        setClickListener();

        fragmentManager = getSupportFragmentManager();

        binding.llMenu01.performClick();

//        getReleaseHashKey();
    }


    // SHA1: 14:80:F5:6C:A9:40:D0:6E:F7:0E:28:A7:A7:9A:FC:A3:7A:EF:57:3A
    private void getReleaseHashKey() {
        byte[] sha1 = {
                0x14, (byte) 0x80, (byte) 0xF5, 0x6C, (byte) 0xA9, 0x40, (byte) 0xD0, 0x6E, (byte) 0xF7, 0x0E, 0x28, (byte) 0xA7, (byte) 0xA7, (byte) 0x9A, (byte) 0xFC, (byte) 0xA3, 0x7A, (byte) 0xEF, 0x57, 0x3A
        };
        Log.e(StringUtil.TAG, "getReleaseHashKey: " + Base64.encodeToString(sha1, Base64.NO_WRAP));
    }

    @Override
    public void onBackPressed() {
        backPressCloseHandler.onBackPressed();
    }


    private void setClickListener() {
        binding.llMenu01.setOnClickListener(this);
        binding.llMenu02.setOnClickListener(this);
        binding.llMenu03.setOnClickListener(this);
        binding.llMenu04.setOnClickListener(this);
    }

    public void replaceFragment(BaseFrag frag, String tag) {
        /* replace fragment */
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.replace_area, frag, tag);
        transaction.commit();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        setResult(RESULT_CANCELED);

    }

    private void switchLayout(View v) {
        binding.llMenu01.setSelected(false);
        binding.llMenu02.setSelected(false);
        binding.llMenu03.setSelected(false);
        binding.llMenu04.setSelected(false);

        v.setSelected(true);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_menu_01:
                switchLayout(v);
                homeFrag = new HomeFrag();

                replaceFragment(homeFrag, TAG_HOME);
                break;

            case R.id.ll_menu_02:
                switchLayout(v);
                talkFrag = new TalkFrag();

                replaceFragment(talkFrag, TAG_TALK);
                break;

            case R.id.ll_menu_03:
                switchLayout(v);
                chatFrag = new ChatFrag();


                replaceFragment(chatFrag, TAG_CHAT);
                break;

            case R.id.ll_menu_04:
                switchLayout(v);
                meFrag = new MeFrag();

                replaceFragment(meFrag, TAG_ME);
                break;

        }
    }
}
