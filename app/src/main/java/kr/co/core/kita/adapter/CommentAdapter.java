package kr.co.core.kita.adapter;

import android.app.Activity;
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

        // 댓글작성시간
        holder.tv_reg_date.setText(data.getReg_date());

        // 프로필 사진
        if(StringUtil.isNull(data.getProfile_img())) {
            Glide.with(act)
                    .load(R.drawable.img_chatlist_noimg)
                    .centerCrop()
                    .transform(new CircleCrop())
                    .into(holder.iv_profile);
        } else {
            Glide.with(act)
                    .load(data.getProfile_img())
                    .centerCrop()
                    .transform(new CircleCrop())
                    .into(holder.iv_profile);
        }
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
