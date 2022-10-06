package com.oktested.videoPlayer.ui;

import com.oktested.core.model.FavouriteResponse;
import com.oktested.core.model.UnFavouriteResponse;
import com.oktested.core.ui.MasterView;
import com.oktested.dashboard.model.GetUserResponse;
import com.oktested.entity.EventTrackingResponse;
import com.oktested.entity.EventTrackingUpdateResponse;
import com.oktested.reportProblem.model.ReportProblemResponse;
import com.oktested.videoPlayer.model.CommentDeleteResponse;
import com.oktested.videoPlayer.model.CommentReactionResponse;
import com.oktested.videoPlayer.model.CommentReplyPostResponse;
import com.oktested.videoPlayer.model.CommentReplyResponse;
import com.oktested.videoPlayer.model.CommentReportResponse;
import com.oktested.videoPlayer.model.FollowResponse;
import com.oktested.videoPlayer.model.ReactionResponse;
import com.oktested.videoPlayer.model.ReplyDeleteResponse;
import com.oktested.videoPlayer.model.ReplyReactionResponse;
import com.oktested.videoPlayer.model.ReplyReportResponse;
import com.oktested.videoPlayer.model.ReportIssueResponse;
import com.oktested.videoPlayer.model.UnFollowResponse;
import com.oktested.videoPlayer.model.VideoCommentPostResponse;
import com.oktested.videoPlayer.model.VideoCommentResponse;

public interface VideoPlayerView extends MasterView {

    void setReactionResponse(ReactionResponse reactionResponse);

    void setUnFavResponse(UnFavouriteResponse unFavouriteResponse);

    void setFavResponse(FavouriteResponse favouriteResponse);

    void setFollowResponse(FollowResponse followResponse);

    void setUnFollowResponse(UnFollowResponse unFollowResponse);

    void getUserData(GetUserResponse getUserResponse);

    void setReportIssueResponse(ReportIssueResponse reportIssueResponse);

    void setReportIssuePostResponse(ReportProblemResponse reportProblemResponse);

    void setVideoCommentResponse(VideoCommentResponse videoCommentResponse);

    void setVideoCommentPostResponse(VideoCommentPostResponse videoCommentPostResponse);

    void setCommentReactionResponse(CommentReactionResponse commentReactionResponse);

    void setCommentDeleteResponse(CommentDeleteResponse commentDeleteResponse);

    void setCommentReportResponse(CommentReportResponse commentReportResponse);

    void setCommentReplyResponse(CommentReplyResponse commentReplyResponse);

    void setCommentReplyPostResponse(CommentReplyPostResponse commentReplyPostResponse);

    void setReplyReactionResponse(ReplyReactionResponse replyReactionResponse);

    void setReplyDeleteResponse(ReplyDeleteResponse replyDeleteResponse);

    void setReplyReportResponse(ReplyReportResponse replyReportResponse);

    void setEventTrackingUpdateResponse(EventTrackingUpdateResponse eventTrackingUpdateResponse);

    void setEventTrackingResponse(EventTrackingResponse eventTrackingResponse);
}