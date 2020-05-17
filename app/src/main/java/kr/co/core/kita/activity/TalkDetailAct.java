package kr.co.core.kita.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;

import java.util.ArrayList;

import jp.wasabeef.glide.transformations.BlurTransformation;
import kr.co.core.kita.R;
import kr.co.core.kita.adapter.CommentAdapter;
import kr.co.core.kita.adapter.ImagePagerAdapter;
import kr.co.core.kita.data.CommentData;
import kr.co.core.kita.databinding.ActivityTalkDetailBinding;

public class TalkDetailAct extends BaseAct implements View.OnClickListener {
    private ActivityTalkDetailBinding binding;
    private Activity act;

    private ImagePagerAdapter imagePagerAdapter;
    private ArrayList<String> imageList = new ArrayList<>();

    private CommentAdapter adapter;
    private ArrayList<CommentData> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_talk_detail, null);
        act = this;

        // 클릭 리스너
        binding.flBack.setOnClickListener(this);

        //테스트데이터 세팅
        Glide.with(act)
                .load(R.drawable.dongsuk)
                .transform(new CircleCrop())
                .into(binding.ivProfile);

        // 토크 이미지 세팅
        imageList.add("test");
        imageList.add("test");
        imageList.add("test");
        imageList.add("test");
        imagePagerAdapter = new ImagePagerAdapter(getSupportFragmentManager(), imageList);
        binding.imagePager.setAdapter(imagePagerAdapter);
        binding.pageIndicator.attachTo(binding.imagePager);


        // 토크 댓글 세팅
        setTestData();

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(act));
        binding.recyclerView.setItemViewCacheSize(20);
        binding.recyclerView.setHasFixedSize(true);

        adapter = new CommentAdapter(act, list);
        binding.recyclerView.setAdapter(adapter);
    }

    private void setTestData() {
        for (int i = 0; i < 10; i++) {
            list.add(new CommentData(null, "마동석" + i, "It is comment content It is comment ", "2020.02.09 15:30",null));
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fl_back:
                finish();
                break;
        }
    }
}
