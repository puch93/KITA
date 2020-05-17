package kr.co.core.kita.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import kr.co.core.kita.R;
import kr.co.core.kita.activity.BaseAct;
import kr.co.core.kita.databinding.ActivityMainBinding;
import kr.co.core.kita.fragment.BaseFrag;
import kr.co.core.kita.fragment.ChatFrag;
import kr.co.core.kita.fragment.HomeFrag;
import kr.co.core.kita.fragment.MeFrag;
import kr.co.core.kita.fragment.TalkFrag;

public class MainAct extends BaseAct implements View.OnClickListener {
    ActivityMainBinding binding;
    Activity act;
    FragmentManager fragmentManager;

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

        setClickListener();

        fragmentManager = getSupportFragmentManager();

        binding.llMenu01.performClick();
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
