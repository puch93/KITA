package kr.co.core.kita.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import kr.co.core.kita.R;

public class HomeLocationAdapter extends RecyclerView.Adapter<HomeLocationAdapter.ViewHolder> {
    List<String> list;
    Activity act;

    public HomeLocationAdapter(Activity act, List<String> list) {
        this.list = list;
        this.act = act;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_home_location, parent, false);
        return new HomeLocationAdapter.ViewHolder(view);
    }

    public void setList(List<String> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final String data = list.get(position);

        holder.tv_location.setText(data);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_location;
        View itemView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_location = itemView.findViewById(R.id.tv_location);
            this.itemView = itemView;
        }
    }
}
