package com.oktested.community.presenter;

import android.content.Context;
import android.provider.Settings;

import com.google.gson.JsonObject;
import com.oktested.community.model.PollSubmitResponse;
import com.oktested.community.model.PostDetailResponse;
import com.oktested.community.model.PostLogResponse;
import com.oktested.community.ui.PostDetailView;
import com.oktested.core.model.BaseResponse;
import com.oktested.core.network.NetworkHandler;
import com.oktested.core.network.NetworkManager;
import com.oktested.home.model.PostReactionResponse;
import com.oktested.home.model.PostShareResponse;
import com.oktested.utils.AppConstants;
import com.oktested.utils.AppPreferences;
import com.oktested.utils.Helper;
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

import java.util.HashMap;

import okhttp3.Request;

public class PostDetailPresenter extends NetworkHandler {

    private Context context;
    private PostDetailView postDetailView;

    public PostDetailPresenter(Context context, PostDetailView postDetailView) {
        super(context, postDetailView);
        this.context = context;
        this.postDetailView = postDetailView;
    }

    public void getPostDetailData(String postId) {
        HashMap<String, String> headerMap = new HashMap<>();
        headerMap.put("Authorization", "Bearer " + AppPreferences.getInstance(context).getPreferencesString(AppConstants.ACCESS_TOKEN));
        NetworkManager.getApi().getSinglePostDetailData(headerMap, postId).enqueue(this);
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

    public void postUserComment(String postId, String comment, String community_id) {
        HashMap<String, String> headerMap = new HashMap<>();
        headerMap.put("Authorization", "Bearer " + AppPreferences.getInstance(context).getPreferencesString(AppConstants.ACCESS_TOKEN));
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("userid", AppPreferences.getInstance(context).getPreferencesString(AppConstants.UID));
        jsonObject.addProperty("post_id", postId);
        jsonObject.addProperty("community_id", community_id);
        jsonObject.addProperty("slug", "community-post");
        jsonObject.addProperty("comment", comment);
        jsonObject.addProperty("device_type", "android");
        jsonObject.addProperty("parent_id", "");
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
        jsonObject.addProperty("device_type", "android");
        jsonObject.addProperty("content_type", "post-comment");
        NetworkManager.getApi().sendCommentReaction(headerMap, jsonObject).enqueue(this);
    }

    public void deleteComment(String commentId) {
        HashMap<String, String> headerMap = new HashMap<>();
        headerMap.put("Authorization", "Bearer " + AppPreferences.getInstance(context).getPreferencesString(AppConstants.ACCESS_TOKEN));
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("userid", AppPreferences.getInstance(context).getPreferencesString(AppConstants.UID));
        jsonObject.addProperty("id", commentId);
        jsonObject.addProperty("device_type", "android");
        NetworkManager.getApi().deleteComment(headerMap, jsonObject).enqueue(this);
    }

    public void reportComment(String commentId, String postId, String community_id) {
        HashMap<String, String> headerMap = new HashMap<>();
        headerMap.put("Authorization", "Bearer " + AppPreferences.getInstance(context).getPreferencesString(AppConstants.ACCESS_TOKEN));
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("userid", AppPreferences.getInstance(context).getPreferencesString(AppConstants.UID));
        jsonObject.addProperty("comment_id", commentId);
        jsonObject.addProperty("post_id", postId);
        jsonObject.addProperty("community_id", community_id);
        jsonObject.addProperty("device_type", "android");
        jsonObject.addProperty("parent_id", "");
        NetworkManager.getApi().reportComment(headerMap, jsonObject).enqueue(this);
    }

    public void postCommentReply(String postId, String comment, String parentId, String replyTo, String community_id) {
        HashMap<String, String> headerMap = new HashMap<>();
        headerMap.put("Authorization", "Bearer " + AppPreferences.getInstance(context).getPreferencesString(AppConstants.ACCESS_TOKEN));
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("userid", AppPreferences.getInstance(context).getPreferencesString(AppConstants.UID));
        jsonObject.addProperty("post_id", postId);
        jsonObject.addProperty("slug", "community-post");
        jsonObject.addProperty("community_id", community_id);
        jsonObject.addProperty("device_type", "android");
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
        jsonObject.addProperty("device_type", "android");
        jsonObject.addProperty("content_type", "post-comment");
        NetworkManager.getApi().sendReplyReaction(headerMap, jsonObject).enqueue(this);
    }

    public void deleteReply(String replyId) {
        HashMap<String, String> headerMap = new HashMap<>();
        headerMap.put("Authorization", "Bearer " + AppPreferences.getInstance(context).getPreferencesString(AppConstants.ACCESS_TOKEN));
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("userid", AppPreferences.getInstance(context).getPreferencesString(AppConstants.UID));
        jsonObject.addProperty("id", replyId);
        jsonObject.addProperty("device_type", "android");
        NetworkManager.getApi().deleteReply(headerMap, jsonObject).enqueue(this);
    }

    public void reportReply(String parentId, String postId, String replyId, String community_id) {
        HashMap<String, String> headerMap = new HashMap<>();
        headerMap.put("Authorization", "Bearer " + AppPreferences.getInstance(context).getPreferencesString(AppConstants.ACCESS_TOKEN));
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("userid", AppPreferences.getInstance(context).getPreferencesString(AppConstants.UID));
        jsonObject.addProperty("comment_id", replyId);
        jsonObject.addProperty("post_id", postId);
        jsonObject.addProperty("parent_id", parentId);
        jsonObject.addProperty("community_id", community_id);
        jsonObject.addProperty("device_type", "android");
        NetworkManager.getApi().reportReply(headerMap, jsonObject).enqueue(this);
    }

    public void sendCommunityPostReaction(String type, String postId) {
        HashMap<String, String> headerMap = new HashMap<>();
        headerMap.put("Authorization", "Bearer " + AppPreferences.getInstance(context).getPreferencesString(AppConstants.ACCESS_TOKEN));
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("user_id", AppPreferences.getInstance(context).getPreferencesString(AppConstants.UID));
        jsonObject.addProperty("content_type", "community-post");
        jsonObject.addProperty("device_type", "android");
        jsonObject.addProperty("post_id", postId);
        jsonObject.addProperty("content_id", postId);
        jsonObject.addProperty("parent_id", "");
        jsonObject.addProperty("type", type);
        NetworkManager.getApi().sendPostReaction(headerMap, jsonObject).enqueue(this);
    }

    public void sendCommunityPostShareCount(String postId, String platform, String communityId) {
        HashMap<String, String> headerMap = new HashMap<>();
        headerMap.put("Authorization", "Bearer " + AppPreferences.getInstance(context).getPreferencesString(AppConstants.ACCESS_TOKEN));
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("type", "counted");
        jsonObject.addProperty("device_type", "android");
        jsonObject.addProperty("platform", platform);
        jsonObject.addProperty("post_id", postId);
        jsonObject.addProperty("Community_id", communityId);
        jsonObject.addProperty("device_id", Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID));
        NetworkManager.getApi().sendPostShareCount(headerMap, jsonObject).enqueue(this);
    }

    public void postPoll(String postId, String communityId, String selectedOption) {
        HashMap<String, String> headerMap = new HashMap<>();
        headerMap.put("Authorization", "Bearer " + AppPreferences.getInstance(context).getPreferencesString(AppConstants.ACCESS_TOKEN));
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("post_id", postId);
        jsonObject.addProperty("community_id", communityId);
        jsonObject.addProperty("device_type", "android");
        jsonObject.addProperty("option", selectedOption);
        NetworkManager.getApi().sendPollSelectedOption(headerMap, jsonObject).enqueue(this);
    }

    public void sendPostLogData(String postId, String communityId) {
        HashMap<String, String> headerMap = new HashMap<>();
        headerMap.put("Authorization", "Bearer " + AppPreferences.getInstance(context).getPreferencesString(AppConstants.ACCESS_TOKEN));
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("post_id", postId);
        jsonObject.addProperty("community_id", communityId);
        jsonObject.addProperty("device_type", "android");
        jsonObject.addProperty("type", "community_post");
        NetworkManager.getApi().callPostLogApi(headerMap, jsonObject).enqueue(this);
    }

    public void getCommentDetailedData(String commentId, String postId) {
        HashMap<String, String> headerMap = new HashMap<>();
        headerMap.put("Authorization", "Bearer " + AppPreferences.getInstance(context).getPreferencesString(AppConstants.ACCESS_TOKEN));
        NetworkManager.getApi().getSingleCommentData(headerMap, postId, commentId).enqueue(this);
    }

    @Override
    public boolean handleResponse(Request request, BaseResponse response) {
        if (postDetailView != null) {
            if (response instanceof VideoCommentResponse) {
                VideoCommentResponse videoCommentResponse = (VideoCommentResponse) response;
                postDetailView.setVideoCommentResponse(videoCommentResponse);
            } else if (response instanceof VideoCommentPostResponse) {
                postDetailView.hideLoader();
                VideoCommentPostResponse videoCommentPostResponse = (VideoCommentPostResponse) response;
                postDetailView.setVideoCommentPostResponse(videoCommentPostResponse);
            } else if (response instanceof CommentReactionResponse) {
                CommentReactionResponse commentReactionResponse = (CommentReactionResponse) response;
                postDetailView.setCommentReactionResponse(commentReactionResponse);
            } else if (response instanceof CommentDeleteResponse) {
                CommentDeleteResponse commentDeleteResponse = (CommentDeleteResponse) response;
                postDetailView.setCommentDeleteResponse(commentDeleteResponse);
            } else if (response instanceof CommentReportResponse) {
                CommentReportResponse commentReportResponse = (CommentReportResponse) response;
                postDetailView.setCommentReportResponse(commentReportResponse);
            } else if (response instanceof CommentReplyResponse) {
                CommentReplyResponse commentReplyResponse = (CommentReplyResponse) response;
                postDetailView.setCommentReplyResponse(commentReplyResponse);
            } else if (response instanceof CommentReplyPostResponse) {
                postDetailView.hideLoader();
                CommentReplyPostResponse commentReplyPostResponse = (CommentReplyPostResponse) response;
                postDetailView.setCommentReplyPostResponse(commentReplyPostResponse);
            } else if (response instanceof ReplyDeleteResponse) {
                ReplyDeleteResponse replyDeleteResponse = (ReplyDeleteResponse) response;
                postDetailView.setReplyDeleteResponse(replyDeleteResponse);
            } else if (response instanceof ReplyReactionResponse) {
                ReplyReactionResponse replyReactionResponse = (ReplyReactionResponse) response;
                postDetailView.setReplyReactionResponse(replyReactionResponse);
            } else if (response instanceof ReplyReportResponse) {
                ReplyReportResponse replyReportResponse = (ReplyReportResponse) response;
                postDetailView.setReplyReportResponse(replyReportResponse);
            } else if (response instanceof PostDetailResponse) {
                postDetailView.hideLoader();
                PostDetailResponse postDetailResponse = (PostDetailResponse) response;
                postDetailView.setPostDetailResponse(postDetailResponse);
            } else if (response instanceof PostReactionResponse) {
                PostReactionResponse postReactionResponse = (PostReactionResponse) response;
                postDetailView.setPostReactionResponse(postReactionResponse);
            } else if (response instanceof PostShareResponse) {
                PostShareResponse postShareResponse = (PostShareResponse) response;
                postDetailView.setPostShareResponse(postShareResponse);
            } else if (response instanceof PollSubmitResponse) {
                PollSubmitResponse pollSubmitResponse = (PollSubmitResponse) response;
                postDetailView.setPollSubmitResponse(pollSubmitResponse);
            } else if (response instanceof PostLogResponse) {
                PostLogResponse postLogResponse = (PostLogResponse) response;
                postDetailView.setPostLogResponse(postLogResponse);
            } else if (response instanceof SingleCommentResponse) {
                postDetailView.hideLoader();
                SingleCommentResponse singleCommentResponse = (SingleCommentResponse) response;
                postDetailView.getCommentDetailData(singleCommentResponse);
            }
        }
        return true;
    }

    @Override
    public boolean handleFailure(Request request, Exception ex, String message) {
        if (postDetailView != null) {
            postDetailView.hideLoader();
            if (Helper.isContainValue(message)) {
                postDetailView.showMessage(message);
            }
        }
        return super.handleFailure(request, ex, message);
    }

    @Override
    public boolean handleError(Request request, Error error) {
        if (postDetailView != null) {
            postDetailView.hideLoader();
            postDetailView.showMessage(error.getMessage());
        }
        return super.handleError(request, error);
    }

    public void onDestroyedView() {
        postDetailView = null;
    }
}