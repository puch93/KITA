package kr.co.core.kita.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import kr.co.core.kita.R;
import kr.co.core.kita.adapter.ChattingListAdapter;
import kr.co.core.kita.adapter.ProfileTalkAdapter;
import kr.co.core.kita.data.ChattingLIstData;
import kr.co.core.kita.data.ProfileTalkData;
import kr.co.core.kita.databinding.FragmentChatBinding;
import kr.co.core.kita.databinding.FragmentMeBinding;
import kr.co.core.kita.util.AllOfDecoration;

public class MeFrag extends BaseFrag {
    FragmentMeBinding binding;
    Activity act;

    ProfileTalkAdapter adapter;
    ArrayList<ProfileTalkData> list = new ArrayList<>();


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_me, container, false);
        act = getActivity();

        binding.recyclerView.setLayoutManager(new GridLayoutManager(act, 3));
        binding.recyclerView.setItemViewCacheSize(20);
        binding.recyclerView.setHasFixedSize(true);
        adapter = new ProfileTalkAdapter(act, list);
        binding.recyclerView.setAdapter(adapter);
        binding.recyclerView.addItemDecoration(new AllOfDecoration(act, AllOfDecoration.PROFILE_DETAIL));

        setTestData();

        return binding.getRoot();
    }

    private void setTestData() {
        for (int i = 0; i < 20; i++) {
            list.add(new ProfileTalkData(null, null));
        }

        act.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.setList(list);
            }
        });
    }
}
