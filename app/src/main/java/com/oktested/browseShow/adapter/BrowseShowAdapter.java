package com.oktested.browseShow.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.oktested.R;
import com.oktested.home.model.ShowsListModel;
import com.oktested.showDetail.ui.ShowDetailActivity;
import com.oktested.utils.Helper;

import java.util.ArrayList;

public class BrowseShowAdapter extends RecyclerView.Adapter<BrowseShowAdapter.ViewHolder> {

    private Context context;
    private ArrayList<ShowsListModel> showAL;

    public BrowseShowAdapter(Context context, ArrayList<ShowsListModel> showAL) {
        this.context = context;
        this.showAL = showAL;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.show_anchor_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (Helper.isContainValue(showAL.get(position).topic_name)) {
            holder.titleTV.setText(showAL.get(position).topic_name);
        }
        if (Helper.isContainValue(showAL.get(position).onexone_img)) {
            Glide.with(context)
                    .load(showAL.get(position).onexone_img)
                    .centerCrop()
                    .placeholder(R.drawable.placeholder)
                    .into(holder.videoThumbIV);
        } else {
            holder.videoThumbIV.setImageDrawable(context.getResources().getDrawable(R.drawable.placeholder));
        }

        holder.itemLL.setOnClickListener(view -> {
            Intent intent = new Intent(context, ShowDetailActivity.class);
            intent.putExtra("slug", showAL.get(position).topic_slug);
            intent.putExtra("sw_more", showAL.get(position).sw_more);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return showAL.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView titleTV;
        ImageView videoThumbIV;
        LinearLayout itemLL;

        ViewHolder(View v) {
            super(v);
            videoThumbIV = v.findViewById(R.id.videoThumbIV);
            titleTV = v.findViewById(R.id.titleTV);
            itemLL = v.findViewById(R.id.itemLL);
        }
    }

    public void setMoreShowsData(ArrayList<ShowsListModel> showAL) {
        this.showAL.addAll(showAL);
        notifyDataSetChanged();
    }
}