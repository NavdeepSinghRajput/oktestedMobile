package com.oktested.browseAnchor.adapter;

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
import com.oktested.anchorDetail.ui.AnchorDetailActivity;
import com.oktested.home.model.AnchorsListModel;
import com.oktested.utils.Helper;

import java.util.ArrayList;

public class BrowseAnchorAdapter extends RecyclerView.Adapter<BrowseAnchorAdapter.ViewHolder> {

    private Context context;
    private ArrayList<AnchorsListModel> anchorsAL;

    public BrowseAnchorAdapter(Context context, ArrayList<AnchorsListModel> anchorsAL) {
        this.context = context;
        this.anchorsAL = anchorsAL;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.show_anchor_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (Helper.isContainValue(anchorsAL.get(position).first_name)) {
            holder.titleTV.setText(anchorsAL.get(position).first_name);
        }
        if (Helper.isContainValue(anchorsAL.get(position).profile_pic)) {
            Glide.with(context)
                    .load(anchorsAL.get(position).profile_pic)
                    .centerCrop()
                    .placeholder(R.drawable.placeholder)
                    .into(holder.videoThumbIV);
        } else {
            holder.videoThumbIV.setImageDrawable(context.getResources().getDrawable(R.drawable.placeholder));
        }

        holder.itemLL.setOnClickListener(view -> {
            Intent intent = new Intent(context, AnchorDetailActivity.class);
            intent.putExtra("anchorModel", anchorsAL.get(position));
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return anchorsAL.size();
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
}