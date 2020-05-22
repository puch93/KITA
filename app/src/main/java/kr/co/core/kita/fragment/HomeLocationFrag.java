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
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import kr.co.core.kita.R;
import kr.co.core.kita.adapter.HomeLocationAdapter;
import kr.co.core.kita.databinding.FragmentHomeLocationBinding;
import kr.co.core.kita.util.AppPreference;
import kr.co.core.kita.util.Common;

public class HomeLocationFrag extends BaseFrag implements View.OnClickListener {
    private FragmentHomeLocationBinding binding;
    private Activity act;


    private HomeLocationAdapter adapter;
    private List<String> list;
    private boolean isScroll = false;
    private int page = 1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home_location, container, false);
        act = (AppCompatActivity) getActivity();

        if(AppPreference.getProfilePref(act, AppPreference.PREF_GENDER).equalsIgnoreCase(Common.GENDER_M)) {
            list = Arrays.asList(getResources().getStringArray(R.array.region_select_w));
        } else {
            list = Arrays.asList(getResources().getStringArray(R.array.region_select_m));
        }

        // set recycler view
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(act));
        binding.recyclerView.setItemViewCacheSize(20);
        binding.recyclerView.setHasFixedSize(true);

        adapter = new HomeLocationAdapter(act, list);
        binding.recyclerView.setAdapter(adapter);

        return binding.getRoot();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

        }

    }
}
