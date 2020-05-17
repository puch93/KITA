package kr.co.core.kita.adapter;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;

import java.util.ArrayList;

import jp.wasabeef.glide.transformations.BlurTransformation;
import kr.co.core.kita.R;
import kr.co.core.kita.activity.ProfileDetailAct;
import kr.co.core.kita.data.CommentData;
import kr.co.core.kita.util.StringUtil;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {
    ArrayList<CommentData> list = new ArrayList<>();
    Activity act;

    public CommentAdapter(Activity act, ArrayList<CommentData> list) {
        this.list = list;
        this.act = act;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comment, parent, false);
        return new CommentAdapter.ViewHolder(view);
    }

    public void setList(ArrayList<CommentData> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final CommentData data = list.get(position);

        // 닉네임
        holder.tv_nick.setText(data.getNick());

        // 댓글내용
        holder.tv_contents.setText(data.getContents());


        // 프로필 사진
        Glide.with(act)
//                .load(data.getProfile_img())
                .load(R.drawable.dongsuk)
                .centerCrop()
                .transform(new CircleCrop())
                .into(holder.iv_profile);

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_profile;
        TextView tv_nick, tv_contents, tv_reg_date;
        View itemView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            iv_profile = itemView.findViewById(R.id.iv_profile);
            tv_nick = itemView.findViewById(R.id.tv_nick);
            tv_contents = itemView.findViewById(R.id.tv_contents);
            tv_reg_date = itemView.findViewById(R.id.tv_reg_date);
            this.itemView = itemView;
        }
    }
}
