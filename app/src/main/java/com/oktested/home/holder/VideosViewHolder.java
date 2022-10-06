package com.oktested.home.holder;

import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.oktested.R;
import com.oktested.utils.AppConstants;
import com.oktested.utils.ScaleImageView;

public class VideosViewHolder extends RecyclerView.ViewHolder {

    public TextView topicTV, titleTV;
    public ImageView editorPickIV, moreShowsArrowIV, scoopWhoopShowsArrowIV;
    public ScaleImageView bannerIV;
    public TextView headerTV, showNameTV, scoopWhoopShowNameTV;
    public RelativeLayout editorPickRL, appExclusiveRL, recentlyAddedRL, showsRL, anchorsRL, moreShowsRL, scoopWhoopVideoRL, scoopWhoopShowsRL, scoopWhoopShowVideoRL, middleAdView, mostViewedRL, trendingRL;
    public RecyclerView appExclusiveRV, recentlyAddedRV, showsRV, anchorsRV, moreShowsRV, scoopWhoopVideoRV, scoopWhoopShowVideoRV, scoopWhoopShowsRV, mostViewedRV, trendingRV;

    public VideosViewHolder(@NonNull View itemView, int viewType) {
        super(itemView);
        switch (viewType) {
            case AppConstants.SECTION_EDITOR_PICK:
                editorPickRL = itemView.findViewById(R.id.editorPickRL);
                topicTV = itemView.findViewById(R.id.topicTV);
                titleTV = itemView.findViewById(R.id.titleTV);
                editorPickIV = itemView.findViewById(R.id.editorPickIV);

            case AppConstants.SECTION_BANNER:
                bannerIV = itemView.findViewById(R.id.bannerIV);

            case AppConstants.SECTION_APP_EXCLUSIVE:
                appExclusiveRL = itemView.findViewById(R.id.appExclusiveRL);
                headerTV = itemView.findViewById(R.id.headerTV);
                appExclusiveRV = itemView.findViewById(R.id.appExclusiveRV);

            case AppConstants.SECTION_RECENTLY_ADDED:
                recentlyAddedRL = itemView.findViewById(R.id.recentlyAddedRL);
                headerTV = itemView.findViewById(R.id.headerTV);
                recentlyAddedRV = itemView.findViewById(R.id.recentlyAddedRV);

            case AppConstants.SECTION_TRENDING:
                trendingRL = itemView.findViewById(R.id.trendingRL);
                headerTV = itemView.findViewById(R.id.headerTV);
                trendingRV = itemView.findViewById(R.id.trendingRV);

            case AppConstants.SECTION_MOST_VIEWED:
                mostViewedRL = itemView.findViewById(R.id.mostViewedRL);
                headerTV = itemView.findViewById(R.id.headerTV);
                mostViewedRV = itemView.findViewById(R.id.mostViewedRV);

            case AppConstants.SECTION_SHOWS:
                showsRL = itemView.findViewById(R.id.showsRL);
                headerTV = itemView.findViewById(R.id.headerTV);
                showsRV = itemView.findViewById(R.id.showsRV);

            case AppConstants.SECTION_ANCHORS:
                anchorsRL = itemView.findViewById(R.id.anchorsRL);
                headerTV = itemView.findViewById(R.id.headerTV);
                anchorsRV = itemView.findViewById(R.id.anchorsRV);

            case AppConstants.SECTION_MORE_SHOWS:
                moreShowsArrowIV = itemView.findViewById(R.id.moreShowsArrowIV);
                moreShowsRL = itemView.findViewById(R.id.moreShowsRL);
                headerTV = itemView.findViewById(R.id.headerTV);
                showNameTV = itemView.findViewById(R.id.showNameTV);
                moreShowsRV = itemView.findViewById(R.id.moreShowsRV);

            case AppConstants.SECTION_SW_VIDEOS:
                scoopWhoopVideoRL = itemView.findViewById(R.id.scoopWhoopVideoRL);
                headerTV = itemView.findViewById(R.id.headerTV);
                scoopWhoopVideoRV = itemView.findViewById(R.id.scoopWhoopVideoRV);

            case AppConstants.SECTION_SW_SHOWS:
                scoopWhoopShowsRL = itemView.findViewById(R.id.scoopWhoopShowsRL);
                headerTV = itemView.findViewById(R.id.headerTV);
                scoopWhoopShowsRV = itemView.findViewById(R.id.scoopWhoopShowsRV);

            case AppConstants.SECTION_SW_SHOWS_VIDEO:
                scoopWhoopShowsArrowIV = itemView.findViewById(R.id.scoopWhoopShowsArrowIV);
                scoopWhoopShowVideoRL = itemView.findViewById(R.id.scoopWhoopShowVideoRL);
                headerTV = itemView.findViewById(R.id.headerTV);
                scoopWhoopShowNameTV = itemView.findViewById(R.id.scoopWhoopShowNameTV);
                scoopWhoopShowVideoRV = itemView.findViewById(R.id.scoopWhoopShowVideoRV);

            case AppConstants.SECTION_BANNER_AD:
                middleAdView = itemView.findViewById(R.id.middleAdView);
        }
    }
}