package kr.co.core.kita.dialog;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import kr.co.core.kita.R;
import kr.co.core.kita.adapter.SelectAdapter;
import kr.co.core.kita.data.SelectData;
import kr.co.core.kita.databinding.DialogSelectBinding;
import kr.co.core.kita.util.StringUtil;


public class SelectDlg extends BaseDlg {
    DialogSelectBinding binding;
    Activity act;

    public static final String TYPE_REGION_M = "Male";
    public static final String TYPE_REGION_W = "Female";
    public static final String TYPE_GENDER = "gender";

    private List array;
    private ArrayList<SelectData> list = new ArrayList<>();
    private int selectedPos = -1;
    private String selectedData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        act = this;

        binding = DataBindingUtil.setContentView(this, R.layout.dialog_select, null);

        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        getWindow().setAttributes(params);

        setFinishOnTouchOutside(false);

        switch (getIntent().getStringExtra("type")) {
            case TYPE_REGION_W:
                array = Arrays.asList(getResources().getStringArray(R.array.region_select_w));
                break;
            case TYPE_REGION_M:
                array = Arrays.asList(getResources().getStringArray(R.array.region_select_m));
                break;
            case TYPE_GENDER:
                array = Arrays.asList(getResources().getStringArray(R.array.gender_select));
                break;
        }

        String data = getIntent().getStringExtra("data");

        // 선택된 데이터 선택 표시
        for (int i = 0; i < array.size(); i++) {
            if (!StringUtil.isNull(data) && ((String) array.get(i)).equalsIgnoreCase(data)) {
                list.add(new SelectData((String) array.get(i), true));
                selectedPos = i;
            } else {
                list.add(new SelectData((String) array.get(i), false));
            }
        }

        // 리싸이클러뷰 설정
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(act));
        binding.recyclerView.setHasFixedSize(true);
        binding.recyclerView.setItemViewCacheSize(20);
        binding.recyclerView.setAdapter(new SelectAdapter(act, list, selectedPos, new SelectAdapter.InterClickListener() {
            @Override
            public void select(String data) {
                selectedData = data;
            }
        }));
        binding.recyclerView.scrollToPosition(selectedPos);


        binding.tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        binding.tvComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("value", selectedData);
                act.setResult(RESULT_OK, intent);
                act.finish();
            }
        });
    }
}
