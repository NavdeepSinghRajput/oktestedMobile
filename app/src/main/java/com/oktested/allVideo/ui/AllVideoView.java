package com.oktested.allVideo.ui;

import com.oktested.core.ui.MasterView;
import com.oktested.home.model.AppExclusiveResponse;
import com.oktested.home.model.MostViewedVideoResponse;
import com.oktested.home.model.ScoopWhoopShowVideoResponse;
import com.oktested.home.model.ScoopWhoopVideoResponse;
import com.oktested.home.model.ShowsVideoResponse;
import com.oktested.home.model.TrendingVideoResponse;
import com.oktested.home.model.VideoListResponse;

public interface AllVideoView extends MasterView {
    void setAppExclusiveResponse(AppExclusiveResponse appExclusiveResponse);

    void setRecentlyAddedResponse(VideoListResponse videoListResponse);

    void setTrendingVideoResponse(TrendingVideoResponse trendingVideoResponse);

    void setMostViewedVideoResponse(MostViewedVideoResponse mostViewedVideoResponse);

    void setShowsVideoResponse(ShowsVideoResponse showsVideoResponse);

    void setScoopWhoopVideoResponse(ScoopWhoopVideoResponse scoopWhoopVideoResponse);

    void setScoopWhoopShowVideoResponse(ScoopWhoopShowVideoResponse scoopWhoopShowVideoResponse);
}