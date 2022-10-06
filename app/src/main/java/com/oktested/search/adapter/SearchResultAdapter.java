package com.oktested.search.adapter;

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
import com.oktested.entity.DataItem;
import com.oktested.utils.Helper;
import com.oktested.videoPlayer.ui.VideoPlayerActivity;

import java.util.ArrayList;

public class SearchResultAdapter extends RecyclerView.Adapter<SearchResultAdapter.ViewHolder> {

    private Context context;
    private ArrayList<DataItem> dataItemList;

    public SearchResultAdapter(Context context, ArrayList<DataItem> dataItemList) {
        this.context = context;
        this.dataItemList = dataItemList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.favourite_video_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (Helper.isContainValue(dataItemList.get(position).show.topic)) {
            holder.topicTV.setText(dataItemList.get(position).show.topic);
        }
        if (Helper.isContainValue(dataItemList.get(position).title)) {
            holder.titleTV.setText(dataItemList.get(position).title);
        }
        if (Helper.isContainValue(dataItemList.get(position).onexone_img)) {
            Glide.with(context)
                    .load(dataItemList.get(position).onexone_img)
                    .centerCrop()
                    .placeholder(R.drawable.placeholder)
                    .into(holder.videoThumbIV);
        } else {
            holder.videoThumbIV.setImageDrawable(context.getResources().getDrawable(R.drawable.placeholder));
        }

        setOnItemClick(holder.itemLL, position);
    }

    @Override
    public int getItemCount() {
        return dataItemList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView topicTV;
        TextView titleTV;
        ImageView videoThumbIV;
        LinearLayout itemLL;

        ViewHolder(View v) {
            super(v);
            itemLL = v.findViewById(R.id.itemLL);
            videoThumbIV = v.findViewById(R.id.videoThumbIV);
            topicTV = v.findViewById(R.id.topicTV);
            titleTV = v.findViewById(R.id.titleTV);
        }
    }

    private void setOnItemClick(LinearLayout itemLL, int position){
        itemLL.setOnClickListener(view -> {
            Intent intent = new Intent(context, VideoPlayerActivity.class);
            intent.putParcelableArrayListExtra("dataItemList", dataItemList);
            intent.putExtra("position", position);
            context.startActivity(intent);
        });
    }

    public void setMoreVideosData(ArrayList<DataItem> dataItemList) {
        this.dataItemList.addAll(dataItemList);
        notifyDataSetChanged();
    }

    public void setMoreVideosDataFirstTime(ArrayList<DataItem> dataItemList) {
        this.dataItemList.clear();
        this.dataItemList.addAll(dataItemList);
        notifyDataSetChanged();
    }
}