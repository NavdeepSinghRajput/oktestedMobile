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
import com.oktested.browseShow.ui.BrowseShowActivity;
import com.oktested.home.model.ShowsListModel;
import com.oktested.showDetail.ui.ShowDetailActivity;
import com.oktested.utils.AppConstants;
import com.oktested.utils.Helper;

import java.util.ArrayList;

public class ShowsAdapter extends RecyclerView.Adapter<ShowsAdapter.ViewHolder> {

    private Context context;
    private ArrayList<ShowsListModel> showsAL;
    private String offset;
    private String screenName;
    private String sectionTitle;

    public ShowsAdapter(Context context, ArrayList<ShowsListModel> showsAL, String offset, String screenName, String sectionTitle) {
        this.context = context;
        this.showsAL = showsAL;
        this.offset = offset;
        this.screenName = screenName;
        this.sectionTitle = sectionTitle;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.shows_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (position == showsAL.size()) {
            holder.viewAllCV.setVisibility(View.VISIBLE);
            holder.itemLL.setVisibility(View.GONE);
        } else {
            holder.viewAllCV.setVisibility(View.GONE);
            holder.itemLL.setVisibility(View.VISIBLE);

            if (Helper.isContainValue(showsAL.get(position).topic_name)) {
                holder.titleTV.setText(showsAL.get(position).topic_name);
            }
            if (Helper.isContainValue(showsAL.get(position).onexone_img)) {
                Glide.with(context)
                        .load(showsAL.get(position).onexone_img)
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
        return showsAL.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView titleTV;
        ImageView videoThumbIV;
        CardView viewAllCV;
        LinearLayout itemLL;

        ViewHolder(View v) {
            super(v);
            itemLL = v.findViewById(R.id.itemLL);
            viewAllCV = v.findViewById(R.id.viewAllCV);
            videoThumbIV = v.findViewById(R.id.videoThumbIV);
            titleTV = v.findViewById(R.id.titleTV);
        }
    }

    private void setOnViewAllClick(CardView itemLL) {
        itemLL.setOnClickListener(view -> {
            Intent intent = new Intent(context, BrowseShowActivity.class);
            intent.putParcelableArrayListExtra("showModel", showsAL);
            intent.putExtra("offset", offset);
            intent.putExtra("screenName", screenName);
            if (screenName.equalsIgnoreCase(AppConstants.SECTION_SHOWS_LAYOUT)) {
                intent.putExtra("heading", "Our Shows");
            } else {
                intent.putExtra("heading", sectionTitle);
            }
            context.startActivity(intent);
        });
    }

    private void setOnItemClick(LinearLayout viewAllCV, int position) {
        viewAllCV.setOnClickListener(view -> {
            Intent intent = new Intent(context, ShowDetailActivity.class);
            intent.putExtra("slug", showsAL.get(position).topic_slug);
            intent.putExtra("sw_more", showsAL.get(position).sw_more);
            context.startActivity(intent);
        });
    }
}