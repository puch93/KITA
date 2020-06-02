package kr.co.core.kita.adapter;

import android.app.Activity;
import android.content.Intent;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import kr.co.core.kita.R;
import kr.co.core.kita.activity.ChatAct;
import kr.co.core.kita.activity.ProfileDetailAct;
import kr.co.core.kita.data.HomeMemberData;
import kr.co.core.kita.server.ReqBasic;
import kr.co.core.kita.server.netUtil.HttpResult;
import kr.co.core.kita.server.netUtil.NetUrls;
import kr.co.core.kita.util.AppPreference;
import kr.co.core.kita.util.Common;
import kr.co.core.kita.util.StringUtil;

public class HomeMemberAdapter extends RecyclerView.Adapter<HomeMemberAdapter.ViewHolder> {
    ArrayList<HomeMemberData> list = new ArrayList<>();
    Activity act;

    public HomeMemberAdapter(Activity act, ArrayList<HomeMemberData> list) {
        this.list = list;
        this.act = act;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_home_member, parent, false);
        HomeMemberAdapter.ViewHolder viewHolder = new HomeMemberAdapter.ViewHolder(view);
        int height = (parent.getMeasuredWidth() - act.getResources().getDimensionPixelSize(R.dimen.size_home_item_minus)) / 2;

        if (height <= 0) {
            height = act.getResources().getDimensionPixelSize(R.dimen.size_home_item_default);
        } else {
//            height = (int) (height * 1.42);
            height = (int) (height * 0.8);
        }

        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) viewHolder.card_view.getLayoutParams();
        params.height = height;
        return viewHolder;
    }

    public void setList(ArrayList<HomeMemberData> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final HomeMemberData data = list.get(position);

        // 닉네임
        holder.tv_nick.setText(data.getNick());

        // 소개글
        holder.tv_intro.setText(data.getIntro());

        if(!StringUtil.isNull(data.getProfile_img())) {
            // 프사 유
            Glide.with(act)
                    .load(data.getProfile_img())
                    .centerCrop()
                    .into(holder.iv_profile);
        } else {
            //프사 무
            Glide.with(act)
                    .load(R.drawable.img_noimg01)
                    .centerCrop()
                    .into(holder.iv_profile);
        }

        // 로그인 상태
        holder.iv_login_state.setSelected(data.isLogin());

        // 상대 프로필 가기
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                act.startActivity(new Intent(act, ProfileDetailAct.class).putExtra("yidx", data.getIdx()));
            }
        });

        holder.iv_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doCheckRoom(data.getIdx(), data.getProfile_img());
            }
        });
    }

    private void doCheckRoom(String yidx, String p_image1) {
        ReqBasic server = new ReqBasic(act, NetUrls.CREATE_ROOM) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {
                if (resultData.getResult() != null) {
                    try {
                        JSONObject jo = new JSONObject(resultData.getResult());
                        JSONObject job = jo.getJSONObject("data");
                        String room_idx = StringUtil.getStr(job, "room_idx");
                        act.startActivity(new Intent(act, ChatAct.class)
                                .putExtra("room_idx", room_idx)
                                .putExtra("yidx", yidx)
                                .putExtra("otherImage", p_image1)
                        );

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Common.showToastNetwork(act);
                    }
                } else {
                    Common.showToastNetwork(act);
                }
            }
        };

        server.setTag("Create Room");
        server.addParams("type", "common");
        server.addParams("uidx", AppPreference.getProfilePref(act, AppPreference.PREF_MIDX));
        server.addParams("tidx", yidx);
        server.execute(true, false);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_profile, iv_login_state, iv_chat;
        TextView tv_nick, tv_intro;
        CardView card_view;
        View itemView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            iv_profile = itemView.findViewById(R.id.iv_profile);
            iv_login_state = itemView.findViewById(R.id.iv_login_state);
            iv_chat = itemView.findViewById(R.id.iv_chat);
            tv_nick = itemView.findViewById(R.id.tv_nick);
            tv_intro = itemView.findViewById(R.id.tv_intro);
            card_view = itemView.findViewById(R.id.card_view);
            this.itemView = itemView;
        }
    }
}
