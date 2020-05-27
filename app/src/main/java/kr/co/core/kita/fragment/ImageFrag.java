package kr.co.core.kita.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;

import kr.co.core.kita.R;
import kr.co.core.kita.activity.EnlargeAct;
import kr.co.core.kita.databinding.FragmentTalkImageBinding;

public class ImageFrag extends BaseFrag {
    private FragmentTalkImageBinding binding;
    private Activity act;
    private String image;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_talk_image, container, false);
        act = (AppCompatActivity) getActivity();

        Glide.with(act)
                .load(image)
                .centerCrop()
                .into(binding.ivProfile);

        binding.ivProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(act, EnlargeAct.class).putExtra("imageUrl", image));
            }
        });

        return binding.getRoot();
    }

    public void setData(String data) {
        image = data;
    }
}
