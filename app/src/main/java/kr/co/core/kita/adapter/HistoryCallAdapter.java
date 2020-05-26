package kr.co.core.kita.adapter;

import android.app.Activity;
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

import java.util.ArrayList;

import kr.co.core.kita.R;
import kr.co.core.kita.data.HistoryCallData;

public class HistoryCallAdapter extends RecyclerView.Adapter<HistoryCallAdapter.ViewHolder> {
    ArrayList<HistoryCallData> list = new ArrayList<>();
    Activity act;

    public HistoryCallAdapter(Activity act, ArrayList<HistoryCallData> list) {
        this.list = list;
        this.act = act;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_history_call, parent, false);
        HistoryCallAdapter.ViewHolder viewHolder = new HistoryCallAdapter.ViewHolder(view);
        int height = (parent.getMeasuredWidth() - act.getResources().getDimensionPixelSize(R.dimen.size_home_item_minus)) / 2;

        if(height <= 0) {
            height = act.getResources().getDimensionPixelSize(R.dimen.size_call_item_default);
        }

        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) viewHolder.card_view.getLayoutParams();
        params.height = height;
        return viewHolder;
    }

    public void setList(ArrayList<HistoryCallData> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final HistoryCallData data = list.get(position);
        // 닉네임
        holder.tv_nick.setText(data.getU_nick());

        // 지역
        holder.tv_region.setText(data.getU_region());

        // 프로필 사진
        Glide.with(act)
//                .load(data.getProfile_img())
                .load(R.drawable.dongsuk)
                .centerCrop()
                .into(holder.iv_profile);



        //통화시간
        holder.tv_call_time.setText(data.getC_time());

        //로그인 상태
        holder.iv_login_state.setSelected(data.isLogin());

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_profile, iv_login_state;
        TextView tv_nick, tv_region, tv_call_time;
        CardView card_view;
        View itemView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            iv_profile = itemView.findViewById(R.id.iv_profile);
            iv_login_state = itemView.findViewById(R.id.iv_login_state);
            tv_nick = itemView.findViewById(R.id.tv_nick);
            tv_region = itemView.findViewById(R.id.tv_region);
            tv_call_time = itemView.findViewById(R.id.tv_call_time);
            card_view = itemView.findViewById(R.id.card_view);
            this.itemView = itemView;
        }
    }
}
