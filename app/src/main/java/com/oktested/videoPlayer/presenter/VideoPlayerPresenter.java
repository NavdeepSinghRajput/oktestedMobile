package com.oktested.videoPlayer.presenter;

import android.content.Context;

import com.google.gson.JsonObject;
import com.oktested.core.model.BaseResponse;
import com.oktested.core.model.FavouriteResponse;
import com.oktested.core.model.UnFavouriteResponse;
import com.oktested.core.network.NetworkHandler;
import com.oktested.core.network.NetworkManager;
import com.oktested.dashboard.model.GetUserResponse;
import com.oktested.entity.EventTrackingResponse;
import com.oktested.entity.EventTrackingUpdateResponse;
import com.oktested.reportProblem.model.ReportProblemResponse;
import com.oktested.utils.AppConstants;
import com.oktested.utils.AppPreferences;
import com.oktested.utils.Helper;
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
import com.oktested.videoPlayer.ui.VideoPlayerView;

import java.util.HashMap;

import okhttp3.Request;

public class VideoPlayerPresenter extends NetworkHandler {

    private Context context;
    private VideoPlayerView videoPlayerView;

    public VideoPlayerPresenter(Context context, VideoPlayerView videoPlayerView) {
        super(context, videoPlayerView);
        this.context = context;
        this.videoPlayerView = videoPlayerView;
    }

    public void callReactionApi(String id, String type) {
        HashMap<String, String> headerMap = new HashMap<>();
        headerMap.put("Authorization", "Bearer " + AppPreferences.getInstance(context).getPreferencesString(AppConstants.ACCESS_TOKEN));
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("video", id);
        jsonObject.addProperty("type", type);
        NetworkManager.getApi().postReaction(headerMap, jsonObject).enqueue(this);
    }

    public void callFollowApi(String username) {
        HashMap<String, String> headerMap = new HashMap<>();
        headerMap.put("Authorization", "Bearer " + AppPreferences.getInstance(context).getPreferencesString(AppConstants.ACCESS_TOKEN));
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("actor", username);
        NetworkManager.getApi().follow(headerMap, jsonObject).enqueue(this);
    }

    public void callUnFollowApi(String username) {
        HashMap<String, String> headerMap = new HashMap<>();
        headerMap.put("Authorization", "Bearer " + AppPreferences.getInstance(context).getPreferencesString(AppConstants.ACCESS_TOKEN));
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("actor", username);
        NetworkManager.getApi().unfollow(headerMap, jsonObject).enqueue(this);
    }

    public void callUnFavouriteApi(String id) {
        HashMap<String, String> headerMap = new HashMap<>();
        headerMap.put("Authorization", "Bearer " + AppPreferences.getInstance(context).getPreferencesString(AppConstants.ACCESS_TOKEN));
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("video", id);
        NetworkManager.getApi().unfavourite(headerMap, jsonObject).enqueue(this);
    }

    public void callFavouriteApi(String id) {
        HashMap<String, String> headerMap = new HashMap<>();
        headerMap.put("Authorization", "Bearer " + AppPreferences.getInstance(context).getPreferencesString(AppConstants.ACCESS_TOKEN));
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("video", id);
        NetworkManager.getApi().favourite(headerMap, jsonObject).enqueue(this);
    }

    public void callUserData() {
        HashMap<String, String> headerMap = new HashMap<>();
        headerMap.put("Authorization", "Bearer " + AppPreferences.getInstance(context).getPreferencesString(AppConstants.ACCESS_TOKEN));
        String id = AppPreferences.getInstance(context).getPreferencesString(AppConstants.UID);
        NetworkManager.getApi().getUserData(headerMap, id).enqueue(this);
    }

    public void getVideoComments(String postId) {
        HashMap<String, String> headerMap = new HashMap<>();
        headerMap.put("Authorization", "Bearer " + AppPreferences.getInstance(context).getPreferencesString(AppConstants.ACCESS_TOKEN));
        NetworkManager.getApi().getVideoCommentData(headerMap, postId).enqueue(this);
    }

    public void getVideoCommentsPagination(String postId, String page) {
        HashMap<String, String> headerMap = new HashMap<>();
        headerMap.put("Authorization", "Bearer " + AppPreferences.getInstance(context).getPreferencesString(AppConstants.ACCESS_TOKEN));
        NetworkManager.getApi().getVideoCommentPagingData(headerMap, postId, page).enqueue(this);
    }

    public void getTopVideoComments(String postId) {
        HashMap<String, String> headerMap = new HashMap<>();
        headerMap.put("Authorization", "Bearer " + AppPreferences.getInstance(context).getPreferencesString(AppConstants.ACCESS_TOKEN));
        NetworkManager.getApi().getTopVideoCommentData(headerMap, postId, "top").enqueue(this);
    }

    public void getTopVideoCommentsPagination(String postId, String page) {
        HashMap<String, String> headerMap = new HashMap<>();
        headerMap.put("Authorization", "Bearer " + AppPreferences.getInstance(context).getPreferencesString(AppConstants.ACCESS_TOKEN));
        NetworkManager.getApi().getTopVideoCommentPagingData(headerMap, postId, "top", page).enqueue(this);
    }

    public void postUserComment(String postId, String slug, String comment) {
        HashMap<String, String> headerMap = new HashMap<>();
        headerMap.put("Authorization", "Bearer " + AppPreferences.getInstance(context).getPreferencesString(AppConstants.ACCESS_TOKEN));
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("userid", AppPreferences.getInstance(context).getPreferencesString(AppConstants.UID));
        jsonObject.addProperty("post_id", postId);
        jsonObject.addProperty("slug", slug);
        jsonObject.addProperty("comment", comment);
        NetworkManager.getApi().sendUserComment(headerMap, jsonObject).enqueue(this);
    }

    public void postCommentReaction(String postId, String commentId, String type) {
        HashMap<String, String> headerMap = new HashMap<>();
        headerMap.put("Authorization", "Bearer " + AppPreferences.getInstance(context).getPreferencesString(AppConstants.ACCESS_TOKEN));
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("user_id", AppPreferences.getInstance(context).getPreferencesString(AppConstants.UID));
        jsonObject.addProperty("post_id", postId);
        jsonObject.addProperty("content_id", commentId);
        jsonObject.addProperty("parent_id", "");
        jsonObject.addProperty("type", type);
        jsonObject.addProperty("content_type", "comment");
        NetworkManager.getApi().sendCommentReaction(headerMap, jsonObject).enqueue(this);
    }

    public void deleteComment(String commentId) {
        HashMap<String, String> headerMap = new HashMap<>();
        headerMap.put("Authorization", "Bearer " + AppPreferences.getInstance(context).getPreferencesString(AppConstants.ACCESS_TOKEN));
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("userid", AppPreferences.getInstance(context).getPreferencesString(AppConstants.UID));
        jsonObject.addProperty("id", commentId);
        NetworkManager.getApi().deleteComment(headerMap, jsonObject).enqueue(this);
    }

    public void reportComment(String commentId, String postId) {
        HashMap<String, String> headerMap = new HashMap<>();
        headerMap.put("Authorization", "Bearer " + AppPreferences.getInstance(context).getPreferencesString(AppConstants.ACCESS_TOKEN));
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("userid", AppPreferences.getInstance(context).getPreferencesString(AppConstants.UID));
        jsonObject.addProperty("comment_id", commentId);
        jsonObject.addProperty("post_id", postId);
        jsonObject.addProperty("parent_id", "");
        NetworkManager.getApi().reportComment(headerMap, jsonObject).enqueue(this);
    }

    public void postCommentReply(String postId, String slug, String comment, String parentId, String replyTo) {
        HashMap<String, String> headerMap = new HashMap<>();
        headerMap.put("Authorization", "Bearer " + AppPreferences.getInstance(context).getPreferencesString(AppConstants.ACCESS_TOKEN));
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("userid", AppPreferences.getInstance(context).getPreferencesString(AppConstants.UID));
        jsonObject.addProperty("post_id", postId);
        jsonObject.addProperty("slug", slug);
        jsonObject.addProperty("comment", comment);
        jsonObject.addProperty("reply_to", replyTo);
        jsonObject.addProperty("parent_id", parentId);
        NetworkManager.getApi().sendCommentReply(headerMap, jsonObject).enqueue(this);
    }

    public void getCommentReply(String postId, String parentId) {
        HashMap<String, String> headerMap = new HashMap<>();
        headerMap.put("Authorization", "Bearer " + AppPreferences.getInstance(context).getPreferencesString(AppConstants.ACCESS_TOKEN));
        NetworkManager.getApi().getCommentReplyData(headerMap, postId, parentId).enqueue(this);
    }

    public void getCommentReplyPagination(String postId, String page, String parentId) {
        HashMap<String, String> headerMap = new HashMap<>();
        headerMap.put("Authorization", "Bearer " + AppPreferences.getInstance(context).getPreferencesString(AppConstants.ACCESS_TOKEN));
        NetworkManager.getApi().getCommentReplyPagingData(headerMap, postId, parentId, page).enqueue(this);
    }

    public void postReplyReaction(String postId, String replyId, String type, String parentId) {
        HashMap<String, String> headerMap = new HashMap<>();
        headerMap.put("Authorization", "Bearer " + AppPreferences.getInstance(context).getPreferencesString(AppConstants.ACCESS_TOKEN));
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("user_id", AppPreferences.getInstance(context).getPreferencesString(AppConstants.UID));
        jsonObject.addProperty("post_id", postId);
        jsonObject.addProperty("content_id", replyId);
        jsonObject.addProperty("parent_id", parentId);
        jsonObject.addProperty("type", type);
        jsonObject.addProperty("content_type", "comment");
        NetworkManager.getApi().sendReplyReaction(headerMap, jsonObject).enqueue(this);
    }

    public void deleteReply(String replyId) {
        HashMap<String, String> headerMap = new HashMap<>();
        headerMap.put("Authorization", "Bearer " + AppPreferences.getInstance(context).getPreferencesString(AppConstants.ACCESS_TOKEN));
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("userid", AppPreferences.getInstance(context).getPreferencesString(AppConstants.UID));
        jsonObject.addProperty("id", replyId);
        NetworkManager.getApi().deleteReply(headerMap, jsonObject).enqueue(this);
    }

    public void reportReply(String parentId, String postId, String replyId) {
        HashMap<String, String> headerMap = new HashMap<>();
        headerMap.put("Authorization", "Bearer " + AppPreferences.getInstance(context).getPreferencesString(AppConstants.ACCESS_TOKEN));
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("userid", AppPreferences.getInstance(context).getPreferencesString(AppConstants.UID));
        jsonObject.addProperty("comment_id", replyId);
        jsonObject.addProperty("post_id", postId);
        jsonObject.addProperty("parent_id", parentId);
        NetworkManager.getApi().reportReply(headerMap, jsonObject).enqueue(this);
    }

    public void callVideoTrackingApi(JsonObject jsonObject) {
        NetworkManager.getScoopWhoopVideoTrackingApi().callVideoTrackingApi(jsonObject)
                .enqueue(this);
    }

    public void callVideoTrackingUpdateApi(JsonObject jsonObject) {
        NetworkManager.getScoopWhoopVideoTrackingApi().callVideoTrackingUpdateApi(jsonObject)
                .enqueue(this);
    }

    public void getVideoReportIssue() {
        HashMap<String, String> headerMap = new HashMap<>();
        headerMap.put("Authorization", "Bearer " + AppPreferences.getInstance(context).getPreferencesString(AppConstants.ACCESS_TOKEN));
        NetworkManager.getApi().getVideoReportIssue(headerMap).enqueue(this);
    }

    public void postReportIssueApi(String text, String id) {
        HashMap<String, String> headerMap = new HashMap<>();
        headerMap.put("Authorization", "Bearer " + AppPreferences.getInstance(context).getPreferencesString(AppConstants.ACCESS_TOKEN));
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("content", text);
        jsonObject.addProperty("video_id", id);
        NetworkManager.getApi().reportProblem(headerMap, jsonObject).enqueue(this);
    }

    @Override
    public boolean handleResponse(Request request, BaseResponse response) {
        if (videoPlayerView != null) {
            if (response instanceof GetUserResponse) {
                videoPlayerView.hideLoader();
                GetUserResponse getUserResponse = (GetUserResponse) response;
                videoPlayerView.getUserData(getUserResponse);
            } else if (response instanceof ReactionResponse) {
                videoPlayerView.hideLoader();
                ReactionResponse reactionResponse = (ReactionResponse) response;
                videoPlayerView.setReactionResponse(reactionResponse);
            } else if (response instanceof FavouriteResponse) {
                videoPlayerView.hideLoader();
                FavouriteResponse favouriteResponse = (FavouriteResponse) response;
                videoPlayerView.setFavResponse(favouriteResponse);
            } else if (response instanceof UnFavouriteResponse) {
                videoPlayerView.hideLoader();
                UnFavouriteResponse unFavouriteResponse = (UnFavouriteResponse) response;
                videoPlayerView.setUnFavResponse(unFavouriteResponse);
            } else if (response instanceof FollowResponse) {
                videoPlayerView.hideLoader();
                FollowResponse followResponse = (FollowResponse) response;
                videoPlayerView.setFollowResponse(followResponse);
            } else if (response instanceof UnFollowResponse) {
                videoPlayerView.hideLoader();
                UnFollowResponse followResponse = (UnFollowResponse) response;
                videoPlayerView.setUnFollowResponse(followResponse);
            } else if (response instanceof ReportIssueResponse) {
                ReportIssueResponse reportIssueResponse = (ReportIssueResponse) response;
                videoPlayerView.setReportIssueResponse(reportIssueResponse);
            } else if (response instanceof ReportProblemResponse) {
                ReportProblemResponse reportProblemResponse = (ReportProblemResponse) response;
                videoPlayerView.setReportIssuePostResponse(reportProblemResponse);
            } else if (response instanceof VideoCommentResponse) {
                VideoCommentResponse videoCommentResponse = (VideoCommentResponse) response;
                videoPlayerView.setVideoCommentResponse(videoCommentResponse);
            } else if (response instanceof VideoCommentPostResponse) {
                videoPlayerView.hideLoader();
                VideoCommentPostResponse videoCommentPostResponse = (VideoCommentPostResponse) response;
                videoPlayerView.setVideoCommentPostResponse(videoCommentPostResponse);
            } else if (response instanceof CommentReactionResponse) {
                CommentReactionResponse commentReactionResponse = (CommentReactionResponse) response;
                videoPlayerView.setCommentReactionResponse(commentReactionResponse);
            } else if (response instanceof CommentDeleteResponse) {
                CommentDeleteResponse commentDeleteResponse = (CommentDeleteResponse) response;
                videoPlayerView.setCommentDeleteResponse(commentDeleteResponse);
            } else if (response instanceof CommentReportResponse) {
                CommentReportResponse commentReportResponse = (CommentReportResponse) response;
                videoPlayerView.setCommentReportResponse(commentReportResponse);
            } else if (response instanceof CommentReplyResponse) {
                CommentReplyResponse commentReplyResponse = (CommentReplyResponse) response;
                videoPlayerView.setCommentReplyResponse(commentReplyResponse);
            } else if (response instanceof CommentReplyPostResponse) {
                videoPlayerView.hideLoader();
                CommentReplyPostResponse commentReplyPostResponse = (CommentReplyPostResponse) response;
                videoPlayerView.setCommentReplyPostResponse(commentReplyPostResponse);
            } else if (response instanceof ReplyDeleteResponse) {
                ReplyDeleteResponse replyDeleteResponse = (ReplyDeleteResponse) response;
                videoPlayerView.setReplyDeleteResponse(replyDeleteResponse);
            } else if (response instanceof ReplyReactionResponse) {
                ReplyReactionResponse replyReactionResponse = (ReplyReactionResponse) response;
                videoPlayerView.setReplyReactionResponse(replyReactionResponse);
            } else if (response instanceof ReplyReportResponse) {
                ReplyReportResponse replyReportResponse = (ReplyReportResponse) response;
                videoPlayerView.setReplyReportResponse(replyReportResponse);
            }  else if (response instanceof EventTrackingUpdateResponse) {
                EventTrackingUpdateResponse eventTrackingUpdateResponse = (EventTrackingUpdateResponse) response;
                videoPlayerView.setEventTrackingUpdateResponse(eventTrackingUpdateResponse);
            } else if (response instanceof EventTrackingResponse) {
                EventTrackingResponse eventTrackingResponse = (EventTrackingResponse) response;
                videoPlayerView.setEventTrackingResponse(eventTrackingResponse);
            }
        }
        return true;
    }

    @Override
    public boolean handleFailure(Request request, Exception ex, String message) {
        if (videoPlayerView != null) {
            videoPlayerView.hideLoader();
            if (Helper.isContainValue(message)) {
                videoPlayerView.showMessage(message);
            }
        }
        return super.handleFailure(request, ex, message);
    }

    @Override
    public boolean handleError(Request request, Error error) {
        if (videoPlayerView != null) {
            videoPlayerView.hideLoader();
            videoPlayerView.showMessage(error.getMessage());
        }
        return super.handleError(request, error);
    }

    public void onDestroyedView() {
        videoPlayerView = null;
    }
}