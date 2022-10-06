package com.oktested.favourite.adapter;

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

public class FavouriteShowAdapter extends RecyclerView.Adapter<FavouriteShowAdapter.ViewHolder> {

    private Context context;
    private ArrayList<ShowsListModel> favShowAL;
    private boolean loadAllData = false;

    public FavouriteShowAdapter(Context context, ArrayList<ShowsListModel> favShowAL) {
        this.context = context;
        this.favShowAL = favShowAL;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.fav_show_anchor_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (Helper.isContainValue(favShowAL.get(position).topic_name)) {
            holder.titleTV.setText(favShowAL.get(position).topic_name);
        }
        if (Helper.isContainValue(favShowAL.get(position).onexone_img)) {
            Glide.with(context)
                    .load(favShowAL.get(position).onexone_img)
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
        if (favShowAL.size() < 7) {
            return favShowAL.size();
        } else {
            if (loadAllData) {
                return favShowAL.size();
            } else {
                return 6;
            }
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView titleTV;
        ImageView videoThumbIV;
        LinearLayout itemLL;

        ViewHolder(View v) {
            super(v);
            itemLL = v.findViewById(R.id.itemLL);
            videoThumbIV = v.findViewById(R.id.videoThumbIV);
            titleTV = v.findViewById(R.id.titleTV);
        }
    }

    public void setFavouriteShowData(boolean loadAllData) {
        this.loadAllData = loadAllData;
        notifyDataSetChanged();
    }

    private void setOnItemClick(LinearLayout itemLL, int position){
        itemLL.setOnClickListener(view -> {
            Intent intent = new Intent(context, ShowDetailActivity.class);
            intent.putExtra("slug", favShowAL.get(position).topic_slug);
            intent.putExtra("sw_more", favShowAL.get(position).sw_more);
            context.startActivity(intent);
        });
    }
}