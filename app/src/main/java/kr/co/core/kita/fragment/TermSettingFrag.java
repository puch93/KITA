package kr.co.core.kita.fragment;


import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import kr.co.core.kita.R;
import kr.co.core.kita.databinding.FragmentTermSettingBinding;

public class TermSettingFrag extends BaseFrag {
    private FragmentTermSettingBinding binding;
    private Activity act;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_term_setting, container, false);
        act = (AppCompatActivity) getActivity();
        return binding.getRoot();
    }

}
