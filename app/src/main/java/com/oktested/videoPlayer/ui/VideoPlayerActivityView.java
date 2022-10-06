package com.oktested.videoPlayer.ui;

import com.oktested.core.ui.MasterView;
import com.oktested.home.model.VideoListResponse;
import com.oktested.videoPlayer.model.SingleCommentResponse;

public interface VideoPlayerActivityView extends MasterView {

    void getVideoData(VideoListResponse videoListResponse);

    void getCommentDetailData(SingleCommentResponse singleCommentResponse);
}