package com.oktested.videoPlayer.adapter;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.oktested.R;
import com.oktested.utils.AppConstants;
import com.oktested.utils.AppPreferences;

import java.util.ArrayList;

public class VideoQualityAdapter extends RecyclerView.Adapter<VideoQualityAdapter.ViewHolder> {

    private Context context;
    private ArrayList<String> videoHeightAL;
    private ArrayList<String> videoWidthAL;
    private int mSelectedItem = 0;
    private boolean firstTime;
    private VideoQuality videoQuality;

    public VideoQualityAdapter(Context context, ArrayList<String> videoHeightAL, ArrayList<String> videoWidthAL, boolean firstTime, VideoQuality videoQuality) {
        this.context = context;
        this.videoWidthAL = videoWidthAL;
        this.videoHeightAL = videoHeightAL;
        this.firstTime = firstTime;
        this.videoQuality = videoQuality;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.video_quality_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (position > 0) {
            if (Integer.parseInt(videoHeightAL.get(position)) > 700) {
                holder.videoHeightRB.setText(Html.fromHtml(videoHeightAL.get(position) + "p <b>HD</b>"));
            } else {
                holder.videoHeightRB.setText(videoHeightAL.get(position) + "p");
            }
        } else {
            holder.videoHeightRB.setText(videoHeightAL.get(position));
        }

        if (firstTime) {
            holder.videoHeightRB.setChecked(AppPreferences.getInstance(context).getPreferencesString(AppConstants.VIDEO_HEIGHT).equalsIgnoreCase(videoHeightAL.get(position)));
        } else {
            holder.videoHeightRB.setChecked(position == mSelectedItem);
        }
        setOnItemClick(holder.videoHeightRB, position);
    }

    @Override
    public int getItemCount() {
        return videoHeightAL.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        RadioButton videoHeightRB;

        ViewHolder(View itemView) {
            super(itemView);
            videoHeightRB = itemView.findViewById(R.id.videoHeightRB);
        }
    }

    private void setOnItemClick(RadioButton videoHeightRB, int position) {
        videoHeightRB.setOnClickListener(view -> {
            firstTime = false;
            AppPreferences.getInstance(context).savePreferencesString(AppConstants.VIDEO_HEIGHT, videoHeightAL.get(position));
            AppPreferences.getInstance(context).savePreferencesString(AppConstants.VIDEO_WIDTH, videoWidthAL.get(position));
            mSelectedItem = position;
            notifyDataSetChanged();
            videoQuality.setVideoQuality();
        });
    }

    public interface VideoQuality {
        void setVideoQuality();
    }
}