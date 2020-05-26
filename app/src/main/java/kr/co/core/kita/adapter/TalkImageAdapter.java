package kr.co.core.kita.adapter;

import android.app.Activity;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import kr.co.core.kita.R;

public class TalkImageAdapter extends RecyclerView.Adapter<TalkImageAdapter.ViewHolder> {

    Activity act;
    ArrayList<Uri> list;
    InterClickListener interClickListener;

    public void setList(ArrayList<Uri> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    public TalkImageAdapter(Activity act, ArrayList<Uri> list, InterClickListener interClickListener) {
        this.act = act;
        this.list = list;
        this.interClickListener = interClickListener;
    }

    public interface InterClickListener {
        void selected();
        void removed();
    }



    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view;
        if (i == 0) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_upload_add_photo, parent, false);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_upload_photo, parent, false);
        }
        return new TalkImageAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int i) {
        if (i == 0) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    interClickListener.selected();
                }
            });

        } else {
            Glide.with(act)
                    .load(list.get(i))
                    .into(holder.iv_image);

            holder.fl_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    list.remove(i);
                    notifyDataSetChanged();

                    interClickListener.removed();
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        FrameLayout fl_delete;
        ImageView iv_image;
        View itemView;

        ViewHolder(View v) {
            super(v);
            iv_image = v.findViewById(R.id.iv_image);
            fl_delete = v.findViewById(R.id.fl_delete);
            itemView = v;
        }
    }

    @Override
    public int getItemViewType(int position) {
        if(position == 0) {
            return 0;
        } else {
            return 1;
        }
//        return super.getItemViewType(position);
    }
}
