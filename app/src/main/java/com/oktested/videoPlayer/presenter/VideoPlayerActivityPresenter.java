package com.oktested.videoPlayer.presenter;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.oktested.core.model.BaseResponse;
import com.oktested.core.network.NetworkHandler;
import com.oktested.core.network.NetworkManager;
import com.oktested.home.model.VideoListResponse;
import com.oktested.utils.AppConstants;
import com.oktested.utils.AppPreferences;
import com.oktested.utils.Helper;
import com.oktested.videoPlayer.model.SingleCommentResponse;
import com.oktested.videoPlayer.ui.VideoPlayerActivityView;

import java.util.HashMap;
import java.util.List;

import okhttp3.Request;

public class VideoPlayerActivityPresenter extends NetworkHandler {

    private Context context;
    private VideoPlayerActivityView videoPlayerActivityView;

    public VideoPlayerActivityPresenter(Context context, VideoPlayerActivityView videoPlayerActivityView) {
        super(context, videoPlayerActivityView);
        this.context = context;
        this.videoPlayerActivityView = videoPlayerActivityView;
    }

    public void callSingleVideoDataApi(List<String> videoList) {
        Gson gson = new GsonBuilder().create();
        JsonArray myCustomArray = gson.toJsonTree(videoList).getAsJsonArray();
        JsonObject jsonObject = new JsonObject();
        jsonObject.add("videos", myCustomArray);
        NetworkManager.getScoopWhoopApi().getFavouriteVideoList(jsonObject.toString()).enqueue(this);
    }

    public void getCommentDetailedData(String commentId, String videoId) {
        HashMap<String, String> headerMap = new HashMap<>();
        headerMap.put("Authorization", "Bearer " + AppPreferences.getInstance(context).getPreferencesString(AppConstants.ACCESS_TOKEN));
        NetworkManager.getApi().getSingleCommentData(headerMap, videoId, commentId).enqueue(this);
    }

    @Override
    public boolean handleResponse(Request request, BaseResponse response) {
        if (videoPlayerActivityView != null) {
            videoPlayerActivityView.hideLoader();
            if (response instanceof VideoListResponse) {
                VideoListResponse videoListResponse = (VideoListResponse) response;
                videoPlayerActivityView.getVideoData(videoListResponse);
            } else if (response instanceof SingleCommentResponse) {
                SingleCommentResponse singleCommentResponse = (SingleCommentResponse) response;
                videoPlayerActivityView.getCommentDetailData(singleCommentResponse);
            }
        }
        return true;
    }

    @Override
    public boolean handleFailure(Request request, Exception ex, String message) {
        if (videoPlayerActivityView != null) {
            videoPlayerActivityView.hideLoader();
            if (Helper.isContainValue(message)) {
                videoPlayerActivityView.showMessage(message);
            }
        }
        return super.handleFailure(request, ex, message);
    }

    @Override
    public boolean handleError(Request request, Error error) {
        if (videoPlayerActivityView != null) {
            videoPlayerActivityView.hideLoader();
            videoPlayerActivityView.showMessage(error.getMessage());
        }
        return super.handleError(request, error);
    }

    public void onDestroyedView() {
        videoPlayerActivityView = null;
    }
}