package kr.co.core.kita.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import kr.co.core.kita.R;
import kr.co.core.kita.activity.CallHistoryAct;
import kr.co.core.kita.activity.SearchAct;
import kr.co.core.kita.adapter.ChattingListAdapter;
import kr.co.core.kita.data.ChattingLIstData;
import kr.co.core.kita.data.TalkListData;
import kr.co.core.kita.databinding.FragmentChatBinding;
import kr.co.core.kita.util.StringUtil;

import static android.app.Activity.RESULT_OK;

public class ChatFrag extends BaseFrag {
    FragmentChatBinding binding;
    Activity act;

    LinearLayoutManager manager;
    ChattingListAdapter adapter;
    ArrayList<ChattingLIstData> list = new ArrayList<>();

    private String search_result = "";

    private static final int SEARCH = 101;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_chat, container, false);
        act = getActivity();

        manager = new LinearLayoutManager(act);
        binding.recyclerView.setLayoutManager(manager);
        binding.recyclerView.setHasFixedSize(true);
        binding.recyclerView.setItemViewCacheSize(20);
        adapter = new ChattingListAdapter(act, list);
        binding.recyclerView.setAdapter(adapter);

        setTestData();

        binding.flHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(act, CallHistoryAct.class));
            }
        });

        binding.flSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(act, SearchAct.class).putExtra("data", search_result), SEARCH);
            }
        });

        return binding.getRoot();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK) {
            switch (requestCode) {
                case SEARCH:
                    search_result = data.getStringExtra("value");

                    if(!StringUtil.isNull(search_result)) {
                        ArrayList<ChattingLIstData> search_list = new ArrayList<>();
                        for (int i = 0; i < list.size(); i++) {
                            if(list.get(i).getContents().contains(search_result)) {
                                search_list.add(list.get(i));
                            }
                        }

                        adapter.setList(search_list);
                    } else {
                        adapter.setList(list);
                    }
                    break;
            }
        }
    }

    private void setTestData() {
        for (int i = 0; i < 10; i++) {
            list.add(new ChattingLIstData(null, null, "KITA JTV online Omise" + i, null, "Welcome to KITA live You..." + i, "2020.02.29", String.valueOf(i)));
        }

        act.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.setList(list);
            }
        });
    }
}
