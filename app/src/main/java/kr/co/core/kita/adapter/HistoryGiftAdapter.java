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
import kr.co.core.kita.activity.ProfileDetailAct;
import kr.co.core.kita.data.HistoryGiftData;

public class HistoryGiftAdapter extends RecyclerView.Adapter<HistoryGiftAdapter.ViewHolder> {
    ArrayList<HistoryGiftData> list = new ArrayList<>();
    Activity act;

    public HistoryGiftAdapter(Activity act, ArrayList<HistoryGiftData> list) {
        this.list = list;
        this.act = act;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_history_gift, parent, false);
        return new HistoryGiftAdapter.ViewHolder(view);
    }

    public void setList(ArrayList<HistoryGiftData> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final HistoryGiftData data = list.get(position);

        // 닉네임
        holder.tv_nick.setText(data.getU_nick());

        // 프로필 사진
        Glide.with(act)
//                .load(data.getProfile_img())
                .load(R.drawable.dongsuk)
                .centerCrop()
                .transform(new CircleCrop())
                .into(holder.iv_profile);

        // 선물 상품이름
        holder.tv_gift_name.setText(data.getG_name());
        //선물 상품 가격
        holder.tv_gift_price.setText(data.getG_price());

        holder.tv_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                act.startActivity(new Intent(act, ProfileDetailAct.class));
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_profile;
        TextView tv_nick, tv_gift_name, tv_gift_price, tv_profile;
        View itemView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            iv_profile = itemView.findViewById(R.id.iv_profile);
            tv_nick = itemView.findViewById(R.id.tv_nick);
            tv_gift_name = itemView.findViewById(R.id.tv_gift_name);
            tv_gift_price = itemView.findViewById(R.id.tv_gift_price);
            tv_profile = itemView.findViewById(R.id.tv_profile);
            this.itemView = itemView;
        }
    }
}
