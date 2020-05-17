package kr.co.core.kita.adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;

import java.util.ArrayList;

import kr.co.core.kita.R;
import kr.co.core.kita.activity.TalkDetailAct;
import kr.co.core.kita.data.TalkListData;

public class TalkListAdapter extends RecyclerView.Adapter<TalkListAdapter.ViewHolder> {
    ArrayList<TalkListData> list = new ArrayList<>();
    Activity act;

    public TalkListAdapter(Activity act, ArrayList<TalkListData> list) {
        this.list = list;
        this.act = act;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_talk_list, parent, false);
        return new TalkListAdapter.ViewHolder(view);
    }

    public void setList(ArrayList<TalkListData> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final TalkListData data = list.get(position);

        // 닉네임
        holder.tv_nick.setText(data.getNick());

        // 토크 내용
        holder.tv_contents.setText(data.getContents());

        // 토크 등록일자
        holder.tv_reg_date.setText(data.getReg_date());

        // 프로필 사진
        Glide.with(act)
//                .load(data.getProfile_img())
                .load(R.drawable.dongsuk)
                .centerCrop()
                .transform(new CircleCrop())
                .into(holder.iv_profile);

        holder.tv_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                act.startActivity(new Intent(act, TalkDetailAct.class));
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_profile;
        TextView tv_nick, tv_contents, tv_reg_date, tv_more;
        View itemView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            iv_profile = itemView.findViewById(R.id.iv_profile);
            tv_nick = itemView.findViewById(R.id.tv_nick);
            tv_contents = itemView.findViewById(R.id.tv_contents);
            tv_reg_date = itemView.findViewById(R.id.tv_reg_date);
            tv_more = itemView.findViewById(R.id.tv_more);
            this.itemView = itemView;
        }
    }
}
