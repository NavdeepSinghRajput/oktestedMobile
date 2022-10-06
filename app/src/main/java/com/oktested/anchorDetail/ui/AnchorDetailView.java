package com.oktested.anchorDetail.ui;

import com.oktested.core.ui.MasterView;
import com.oktested.dashboard.model.GetUserResponse;
import com.oktested.home.model.VideoListResponse;
import com.oktested.videoPlayer.model.FollowResponse;
import com.oktested.videoPlayer.model.UnFollowResponse;

public interface AnchorDetailView extends MasterView {
    void getVideoListResponse(VideoListResponse videoListResponse);

    void getUserData(GetUserResponse getUserResponse);

    void setFollowResponse(FollowResponse followResponse);

    void setUnFollowResponse(UnFollowResponse unFollowResponse);
}