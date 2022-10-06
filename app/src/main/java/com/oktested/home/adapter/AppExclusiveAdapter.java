package com.oktested.home.adapter;

import android.content.Context;
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
import com.oktested.R;
import com.oktested.allVideo.ui.AllVideoActivity;
import com.oktested.entity.DataItem;
import com.oktested.utils.AppConstants;
import com.oktested.utils.Helper;
import com.oktested.videoPlayer.ui.VideoPlayerActivity;

import java.util.ArrayList;

public class AppExclusiveAdapter extends RecyclerView.Adapter<AppExclusiveAdapter.ViewHolder> {

    private Context context;
    private ArrayList<DataItem> dataItemList;
    private String sectionTitle, offset, screenName;

    AppExclusiveAdapter(Context context, ArrayList<DataItem> dataItemList, String sectionTitle, String offset, String screenName) {
        this.context = context;
        this.dataItemList = dataItemList;
        this.sectionTitle = sectionTitle;
        this.offset = offset;
        this.screenName = screenName;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.video_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (position == dataItemList.size()) {
            holder.viewAllCV.setVisibility(View.VISIBLE);
            holder.itemLL.setVisibility(View.GONE);
        } else {
            holder.viewAllCV.setVisibility(View.GONE);
            holder.itemLL.setVisibility(View.VISIBLE);

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
        }

        setOnViewAllClick(holder.viewAllCV);
        setOnItemClick(holder.itemLL, position);
    }

    @Override
    public int getItemCount() {
        return dataItemList.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView topicTV;
        TextView titleTV;
        ImageView videoThumbIV;
        CardView viewAllCV;
        LinearLayout itemLL;

        ViewHolder(View v) {
            super(v);
            itemLL = v.findViewById(R.id.itemLL);
            viewAllCV = v.findViewById(R.id.viewAllCV);
            videoThumbIV = v.findViewById(R.id.videoThumbIV);
            topicTV = v.findViewById(R.id.topicTV);
            titleTV = v.findViewById(R.id.titleTV);
        }
    }

    private void setOnViewAllClick(CardView viewAllCV) {
        viewAllCV.setOnClickListener(view -> {
            Intent intent = new Intent(context, AllVideoActivity.class);
            intent.putExtra("offset", offset);
            intent.putExtra("screenName", screenName);
            intent.putExtra("sectionTitle", sectionTitle);
            intent.putParcelableArrayListExtra("dataItemList", dataItemList);
            context.startActivity(intent);
        });
    }

    private void setOnItemClick(LinearLayout itemLL, int position) {
        itemLL.setOnClickListener(view -> {
            Intent intent = new Intent(context, VideoPlayerActivity.class);
            intent.putParcelableArrayListExtra("dataItemList", dataItemList);
            intent.putExtra("position", position);
            context.startActivity(intent);
        });
    }
}