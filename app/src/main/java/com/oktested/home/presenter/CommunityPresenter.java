package com.oktested.home.presenter;

import android.content.Context;
import android.provider.Settings;

import com.google.gson.JsonObject;
import com.oktested.community.model.PollSubmitResponse;
import com.oktested.core.model.BaseResponse;
import com.oktested.core.network.NetworkHandler;
import com.oktested.core.network.NetworkManager;
import com.oktested.home.model.CommunityPostResponse;
import com.oktested.home.model.PostReactionResponse;
import com.oktested.home.model.PostShareResponse;
import com.oktested.home.ui.CommunityView;
import com.oktested.utils.AppConstants;
import com.oktested.utils.AppPreferences;
import com.oktested.utils.Helper;

import java.util.HashMap;

import okhttp3.Request;

public class CommunityPresenter extends NetworkHandler {

    private Context context;
    private CommunityView communityView;

    public CommunityPresenter(Context context, CommunityView communityView) {
        super(context, communityView);
        this.context = context;
        this.communityView = communityView;
    }

    public void getCommunityPostApi(String page) {
        HashMap<String, String> headerMap = new HashMap<>();
        headerMap.put("Authorization", "Bearer " + AppPreferences.getInstance(context).getPreferencesString(AppConstants.ACCESS_TOKEN));
        NetworkManager.getApi().getLatestCommunityPost(headerMap, page, "created").enqueue(this);
    }

    public void sendCommunityPostReaction(String postId, String type) {
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

    @Override
    public boolean handleError(Request request, Error error) {
        if (communityView != null) {
            communityView.hideLoader();
            communityView.showMessage(error.getMessage());
        }
        return super.handleError(request, error);
    }

    @Override
    public boolean handleFailure(Request request, Exception ex, String message) {
        if (communityView != null) {
            communityView.hideLoader();
            if (Helper.isContainValue(message)) {
                communityView.showMessage(message);
            }
        }
        return super.handleFailure(request, ex, message);
    }

    @Override
    public boolean handleResponse(Request request, BaseResponse response) {
        if (communityView != null) {
            communityView.hideLoader();
            if (response instanceof CommunityPostResponse) {
                CommunityPostResponse communityPostResponse = (CommunityPostResponse) response;
                communityView.setCommunityPostResponse(communityPostResponse);
            } else if (response instanceof PostReactionResponse) {
                PostReactionResponse postReactionResponse = (PostReactionResponse) response;
                communityView.setPostReactionResponse(postReactionResponse);
            } else if (response instanceof PostShareResponse) {
                PostShareResponse postShareResponse = (PostShareResponse) response;
                communityView.setPostShareResponse(postShareResponse);
            } else if (response instanceof PollSubmitResponse) {
                PollSubmitResponse pollSubmitResponse = (PollSubmitResponse) response;
                communityView.setPollSubmitResponse(pollSubmitResponse);
            }
        }
        return true;
    }

    public void onDestroyedView() {
        communityView = null;
    }
}