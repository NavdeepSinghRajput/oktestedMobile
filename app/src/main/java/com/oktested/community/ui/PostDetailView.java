package com.oktested.community.ui;

import com.oktested.community.model.PollSubmitResponse;
import com.oktested.community.model.PostDetailResponse;
import com.oktested.community.model.PostLogResponse;
import com.oktested.core.ui.MasterView;
import com.oktested.home.model.PostReactionResponse;
import com.oktested.home.model.PostShareResponse;
import com.oktested.videoPlayer.model.CommentDeleteResponse;
import com.oktested.videoPlayer.model.CommentReactionResponse;
import com.oktested.videoPlayer.model.CommentReplyPostResponse;
import com.oktested.videoPlayer.model.CommentReplyResponse;
import com.oktested.videoPlayer.model.CommentReportResponse;
import com.oktested.videoPlayer.model.ReplyDeleteResponse;
import com.oktested.videoPlayer.model.ReplyReactionResponse;
import com.oktested.videoPlayer.model.ReplyReportResponse;
import com.oktested.videoPlayer.model.SingleCommentResponse;
import com.oktested.videoPlayer.model.VideoCommentPostResponse;
import com.oktested.videoPlayer.model.VideoCommentResponse;

public interface PostDetailView extends MasterView {

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

    void setPostDetailResponse(PostDetailResponse postDetailResponse);

    void setPostReactionResponse(PostReactionResponse postReactionResponse);

    void setPostShareResponse(PostShareResponse postShareResponse);

    void setPollSubmitResponse(PollSubmitResponse pollSubmitResponse);

    void setPostLogResponse(PostLogResponse postLogResponse);

    void getCommentDetailData(SingleCommentResponse singleCommentResponse);
}