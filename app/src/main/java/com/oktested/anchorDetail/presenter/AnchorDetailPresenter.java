package com.oktested.anchorDetail.presenter;

import android.content.Context;

import com.google.gson.JsonObject;
import com.oktested.anchorDetail.ui.AnchorDetailView;
import com.oktested.core.model.BaseResponse;
import com.oktested.core.network.NetworkHandler;
import com.oktested.core.network.NetworkManager;
import com.oktested.dashboard.model.GetUserResponse;
import com.oktested.home.model.VideoListResponse;
import com.oktested.utils.AppConstants;
import com.oktested.utils.AppPreferences;
import com.oktested.utils.Helper;
import com.oktested.videoPlayer.model.FollowResponse;
import com.oktested.videoPlayer.model.UnFollowResponse;

import java.util.HashMap;

import okhttp3.Request;

public class AnchorDetailPresenter extends NetworkHandler {

    private Context context;
    private AnchorDetailView anchorDetailView;

    public AnchorDetailPresenter(Context context, AnchorDetailView anchorDetailView) {
        super(context, anchorDetailView);
        this.context = context;
        this.anchorDetailView = anchorDetailView;
    }

    public void callActorsVideoListApi(String username) {
        NetworkManager.getScoopWhoopApi().getActorsVideoList(username).enqueue(this);
    }

    public void callActorsVideoPagingListApi(String username, String offset) {
        NetworkManager.getScoopWhoopApi().getActorsVideoPagingList(username, offset).enqueue(this);
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

    public void callUserData() {
        HashMap<String, String> headerMap = new HashMap<>();
        headerMap.put("Authorization", "Bearer " + AppPreferences.getInstance(context).getPreferencesString(AppConstants.ACCESS_TOKEN));
        String id = AppPreferences.getInstance(context).getPreferencesString(AppConstants.UID);
        NetworkManager.getApi().getUserData(headerMap, id).enqueue(this);
    }

    @Override
    public boolean handleResponse(Request request, BaseResponse response) {
        if (anchorDetailView != null) {
            if (response instanceof VideoListResponse) {
                VideoListResponse videoListResponse = (VideoListResponse) response;
                anchorDetailView.getVideoListResponse(videoListResponse);
            } else if (response instanceof FollowResponse) {
                FollowResponse followResponse = (FollowResponse) response;
                anchorDetailView.setFollowResponse(followResponse);
            } else if (response instanceof UnFollowResponse) {
                UnFollowResponse followResponse = (UnFollowResponse) response;
                anchorDetailView.setUnFollowResponse(followResponse);
            } else if (response instanceof GetUserResponse) {
                GetUserResponse getUserResponse = (GetUserResponse) response;
                anchorDetailView.getUserData(getUserResponse);
            }
        }
        return true;
    }

    @Override
    public boolean handleFailure(Request request, Exception ex, String message) {
        if (anchorDetailView != null) {
            if (Helper.isContainValue(message)) {
                anchorDetailView.showMessage(message);
            }
        }
        return super.handleFailure(request, ex, message);
    }

    @Override
    public boolean handleError(Request request, Error error) {
        if (anchorDetailView != null) {
            anchorDetailView.showMessage(error.getMessage());
        }
        return super.handleError(request, error);
    }

    public void onDestroyedView() {
        anchorDetailView = null;
    }
}