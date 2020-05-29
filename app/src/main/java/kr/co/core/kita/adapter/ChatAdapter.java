package kr.co.core.kita.adapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;

import java.util.ArrayList;

import kr.co.core.kita.R;
import kr.co.core.kita.activity.EnlargeAct;
import kr.co.core.kita.data.ChattingData;
import kr.co.core.kita.util.AppPreference;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {
    private static final int TYPE_ME_TEXT = 1;
    private static final int TYPE_ME_IMAGE = 3;

    private static final int TYPE_YOU_TEXT = 2;
    private static final int TYPE_YOU_IMAGE = 4;

    private static final int TYPE_DATE_LINE = 100;

    private Activity act;
    private ArrayList<ChattingData> list;

    private String otherImage;

    private String room_idx;

    public ChatAdapter(Activity act, String room_idx, ArrayList<ChattingData> list,
                       String otherImage) {
        this.act = act;
        this.list = list;
        this.otherImage = otherImage;
        this.room_idx = room_idx;
    }

    public void addItem(ChattingData item) {
        if (list == null) {
            list = new ArrayList<>();
        }
        list.add(item);
        notifyDataSetChanged();
    }

    public void setItem(ChattingData item) {
        list.set(list.size() - 1, item);
        notifyDataSetChanged();
    }

    public void setList(ArrayList<ChattingData> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ChatAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case TYPE_ME_TEXT:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_me_text, parent, false);
                return new ViewHolder1(view);
            case TYPE_YOU_TEXT:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_other_text, parent, false);
                return new ViewHolder2(view);

            case TYPE_ME_IMAGE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_me_image, parent, false);
                return new ViewHolder3(view);
            case TYPE_YOU_IMAGE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_other_image, parent, false);
                return new ViewHolder4(view);


            case TYPE_DATE_LINE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_dateline, parent, false);
                return new ViewHolder100(view);
            default:
                return null;
        }
    }

    @Override
    public int getItemViewType(int position) {
        ChattingData data = list.get(position);

        String type = data.getData_type();

        String midx = AppPreference.getProfilePref(act, AppPreference.PREF_MIDX);

        switch (type) {
            case "text":
                if (data.getUser_idx().equalsIgnoreCase(midx))
                    return TYPE_ME_TEXT;
                else
                    return TYPE_YOU_TEXT;

            case "image":
                if (data.getUser_idx().equalsIgnoreCase(midx))
                    return TYPE_ME_IMAGE;
                else
                    return TYPE_YOU_IMAGE;

            case "dateline":
                return TYPE_DATE_LINE;

            default:
                return super.getItemViewType(position);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ChatAdapter.ViewHolder viewHolder, int i) {
        final ChattingData data = list.get(i);
        int viewType = getItemViewType(i);

        switch (viewType) {
            case TYPE_ME_TEXT:
                ViewHolder1 holder1 = (ViewHolder1) viewHolder;

                // set text contents
                holder1.tv_contents.setText(data.getContents());

                // set send time
                holder1.tv_send_time.setText(data.getSend_time());

                // 읽음처리
                if (data.isRead()) {
                    holder1.tv_read.setVisibility(View.GONE);
                } else {
                    holder1.tv_read.setVisibility(View.VISIBLE);
                }

                break;

            case TYPE_YOU_TEXT:
                ViewHolder2 holder2 = (ViewHolder2) viewHolder;

                // set other data
                setOtherData(holder2.iv_profile);

                // set text contents
                holder2.tv_contents.setText(data.getContents());

                // set send time
                holder2.tv_send_time.setText(data.getSend_time());


                break;

            case TYPE_ME_IMAGE:
                ViewHolder3 holder3 = (ViewHolder3) viewHolder;

                // set image contents
                Glide.with(act)
                        .load(data.getContents())
                        .centerCrop()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .transform(new MultiTransformation<Bitmap>(new CenterCrop(), new RoundedCorners(40)))
                        .into(holder3.iv_send_image);

                // set send time
                holder3.tv_send_time.setText(data.getSend_time());


                holder3.iv_send_image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        act.startActivity(new Intent(act, EnlargeAct.class).putExtra("imageUrl", data.getContents()));
                    }
                });

                // 읽음처리
                if (data.isRead()) {
                    holder3.tv_read.setVisibility(View.GONE);
                } else {
                    holder3.tv_read.setVisibility(View.VISIBLE);
                }
                break;

            case TYPE_YOU_IMAGE:
                ViewHolder4 holder4 = (ViewHolder4) viewHolder;

                // set other data
                setOtherData(holder4.iv_profile);

                // set image contents
                Glide.with(act)
                        .load(data.getContents())
                        .centerCrop()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .transform(new MultiTransformation<Bitmap>(new CenterCrop(), new RoundedCorners(40)))
                        .into(holder4.iv_send_image);

                holder4.iv_send_image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        act.startActivity(new Intent(act, EnlargeAct.class).putExtra("imageUrl", data.getContents()));
                    }
                });

                // set send time
                holder4.tv_send_time.setText(data.getSend_time());

                break;

            case TYPE_DATE_LINE:
                ViewHolder100 holder100 = (ViewHolder100) viewHolder;

                // set date
                holder100.tv_date.setText(data.getDate_line());
                break;
        }

    }

    private void setOtherData(ImageView iv_profile) {
        Glide.with(act)
                .load(otherImage)
                .centerCrop()
                .transform(new CircleCrop())
                .into(iv_profile);
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ViewHolder(@NonNull View view) {
            super(view);
        }
    }

    class ViewHolder1 extends ViewHolder {
        TextView tv_contents, tv_send_time, tv_read;

        ViewHolder1(@NonNull View view) {
            super(view);
            tv_contents = view.findViewById(R.id.tv_contents);
            tv_send_time = view.findViewById(R.id.tv_send_time);
            tv_read = view.findViewById(R.id.tv_read);
        }
    }

    class ViewHolder2 extends ViewHolder {
        TextView tv_contents, tv_send_time, tv_read;
        ImageView iv_profile;

        ViewHolder2(@NonNull View view) {
            super(view);
            tv_contents = view.findViewById(R.id.tv_contents);
            tv_send_time = view.findViewById(R.id.tv_send_time);
            iv_profile = view.findViewById(R.id.iv_profile);
            tv_read = view.findViewById(R.id.tv_read);

        }
    }

    class ViewHolder3 extends ViewHolder {
        TextView tv_send_time, tv_read;
        ImageView iv_send_image;

        ViewHolder3(@NonNull View view) {
            super(view);
            tv_send_time = view.findViewById(R.id.tv_send_time);
            tv_read = view.findViewById(R.id.tv_read);
            iv_send_image = (ImageView) view.findViewById(R.id.iv_send_image);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                iv_send_image.setClipToOutline(true);
            }
        }
    }

    class ViewHolder4 extends ViewHolder {
        TextView tv_send_time, tv_read, tv_contents;
        ImageView iv_profile, iv_send_image;

        ViewHolder4(@NonNull View view) {
            super(view);
            tv_read = view.findViewById(R.id.tv_read);
            iv_profile = view.findViewById(R.id.iv_profile);
            tv_send_time = view.findViewById(R.id.tv_send_time);
            iv_send_image = (ImageView) view.findViewById(R.id.iv_send_image);
            tv_contents = view.findViewById(R.id.tv_contents);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                iv_send_image.setClipToOutline(true);
            }
        }
    }

    class ViewHolder100 extends ViewHolder {
        TextView tv_date;

        ViewHolder100(@NonNull View view) {
            super(view);
            tv_date = view.findViewById(R.id.tv_date);
        }
    }
}
