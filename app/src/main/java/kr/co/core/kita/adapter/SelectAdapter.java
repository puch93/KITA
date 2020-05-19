package kr.co.core.kita.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import kr.co.core.kita.R;
import kr.co.core.kita.data.SelectData;

public class SelectAdapter extends RecyclerView.Adapter<SelectAdapter.ViewHolder> {
    ArrayList<SelectData> list = new ArrayList<>();
    Activity act;

    private int pre_position = -1;

    public interface InterClickListener {
        void select(String data);
    }

    InterClickListener clickListener;


    public SelectAdapter(Activity act, ArrayList<SelectData> list, int selectedPos, InterClickListener clickListener) {
        this.list = list;
        this.act = act;
        this.pre_position = selectedPos;
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_region_select, parent, false);
        return new SelectAdapter.ViewHolder(view);
    }

    public void setList(ArrayList<SelectData> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final SelectData data = list.get(position);

        holder.tv_region.setText(data.getData());
        holder.itemView.setSelected(data.isSelect());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                list.get(position).setSelect(true);

                if (pre_position >= 0 && pre_position != position) {
                    list.get(pre_position).setSelect(false);
                }

                pre_position = position;

                notifyDataSetChanged();

                clickListener.select(data.getData());
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView tv_region;
        View itemView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_region = itemView.findViewById(R.id.tv_region);
            this.itemView = itemView;
        }
    }
}
