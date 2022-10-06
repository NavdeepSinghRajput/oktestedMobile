package com.oktested.home.ui;

import com.oktested.core.ui.MasterView;
import com.oktested.home.model.AnchorsResponse;
import com.oktested.home.model.AppExclusiveResponse;
import com.oktested.home.model.EditorPickResponse;
import com.oktested.home.model.HomeStructureResponse;
import com.oktested.home.model.MostViewedVideoResponse;
import com.oktested.home.model.ScoopWhoopShowVideoResponse;
import com.oktested.home.model.ScoopWhoopShowsResponse;
import com.oktested.home.model.ScoopWhoopVideoResponse;
import com.oktested.home.model.ShowsResponse;
import com.oktested.home.model.ShowsVideoResponse;
import com.oktested.home.model.TrendingVideoResponse;
import com.oktested.home.model.VideoListResponse;

public interface VideosView extends MasterView {
    void setHomeStructureResponse(HomeStructureResponse homeStructureResponse);

    void setEditorPickResponse(EditorPickResponse editorPickResponse);

    void setAppExclusiveResponse(AppExclusiveResponse appExclusiveResponse);

    void setRecentlyAddedResponse(VideoListResponse videoListResponse);

    void setTrendingVideoResponse(TrendingVideoResponse trendingVideoResponse);

    void setMostViewedVideoResponse(MostViewedVideoResponse mostViewedVideoResponse);

    void setShowsResponse(ShowsResponse showsResponse);

    void setAnchorsResponse(AnchorsResponse anchorsResponse);

    void setShowsVideoResponse(ShowsVideoResponse showsVideoResponse);

    void setScoopWhoopVideoResponse(ScoopWhoopVideoResponse scoopWhoopVideoResponse);

    void setScoopWhoopShowsResponse(ScoopWhoopShowsResponse scoopWhoopShowsResponse);

    void setScoopWhoopShowVideoResponse(ScoopWhoopShowVideoResponse scoopWhoopShowVideoResponse);
}