package kr.co.core.kita.adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import kr.co.core.kita.R;
import kr.co.core.kita.activity.TalkDetailAct;
import kr.co.core.kita.data.ProfileTalkData;

public class ProfileTalkAdapter extends RecyclerView.Adapter<ProfileTalkAdapter.ViewHolder> {
    ArrayList<ProfileTalkData> list = new ArrayList<>();
    Activity act;

    public ProfileTalkAdapter(Activity act, ArrayList<ProfileTalkData> list) {
        this.list = list;
        this.act = act;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_profile_talk, parent, false);
        ProfileTalkAdapter.ViewHolder viewHolder = new ProfileTalkAdapter.ViewHolder(view);
        int height = (parent.getMeasuredWidth() - act.getResources().getDimensionPixelSize(R.dimen.size_profile_talk_minus)) / 3;

        if(height <= 0) {
            height = act.getResources().getDimensionPixelSize(R.dimen.size_profile_talk_default);
        }

        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) viewHolder.card_view.getLayoutParams();
        params.height = height;
        return viewHolder;
    }

    public void setList(ArrayList<ProfileTalkData> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final ProfileTalkData data = list.get(position);

        // 프로필 사진
        Glide.with(act)
//                .load(data.getImage())
                .load(R.drawable.dongsuk)
                .into(holder.iv_image);

        //
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                act.startActivity(new Intent(act, TalkDetailAct.class));
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_image;
        CardView card_view;
        View itemView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            iv_image = itemView.findViewById(R.id.iv_image);
            card_view = itemView.findViewById(R.id.card_view);
            this.itemView = itemView;
        }
    }
}