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
import kr.co.core.kita.activity.ChatAct;
import kr.co.core.kita.data.ChattingLIstData;
import kr.co.core.kita.util.StringUtil;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ViewHolder> {
    ArrayList<ChattingLIstData> list = new ArrayList<>();
    Activity act;

    public ChatListAdapter(Activity act, ArrayList<ChattingLIstData> list) {
        this.list = list;
        this.act = act;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chatting_list, parent, false);
        return new ChatListAdapter.ViewHolder(view);
    }

    public void setList(ArrayList<ChattingLIstData> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final ChattingLIstData data = list.get(position);



        // 채팅 내용
        holder.tv_contents.setText(data.getContents());

        // 채팅 등록일자
        holder.tv_reg_date.setText(data.getReg_date());

        // 채팅 안읽은 개수
        if(StringUtil.isNull(data.getRead_count()) || data.getRead_count().equalsIgnoreCase("0")) {
            holder.tv_read_count.setVisibility(View.INVISIBLE);
        } else {
            holder.tv_read_count.setVisibility(View.VISIBLE);
            holder.tv_read_count.setText(data.getRead_count());
        }


        // 닉네임
        holder.tv_nick.setText(data.getU_nick());

        // 프로필 사진
        Glide.with(act)
//                .load(data.getProfile_img())
                .load(R.drawable.dongsuk)
                .centerCrop()
                .transform(new CircleCrop())
                .into(holder.iv_profile);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                act.startActivity(new Intent(act, ChatAct.class));
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_profile;
        TextView tv_nick, tv_contents, tv_reg_date, tv_read_count;
        View itemView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            iv_profile = itemView.findViewById(R.id.iv_profile);
            tv_nick = itemView.findViewById(R.id.tv_nick);
            tv_contents = itemView.findViewById(R.id.tv_contents);
            tv_reg_date = itemView.findViewById(R.id.tv_reg_date);
            tv_read_count = itemView.findViewById(R.id.tv_read_count);
            this.itemView = itemView;
        }
    }
}
