package com.oktested.showDetail.presenter;

import android.content.Context;

import com.google.gson.JsonObject;
import com.oktested.core.model.BaseResponse;
import com.oktested.core.model.FavouriteResponse;
import com.oktested.core.model.UnFavouriteResponse;
import com.oktested.core.network.NetworkHandler;
import com.oktested.core.network.NetworkManager;
import com.oktested.dashboard.model.GetUserResponse;
import com.oktested.home.model.ScoopWhoopShowVideoResponse;
import com.oktested.home.model.ShowsVideoResponse;
import com.oktested.showDetail.ui.ShowDetailView;
import com.oktested.utils.AppConstants;
import com.oktested.utils.AppPreferences;
import com.oktested.utils.Helper;

import java.util.HashMap;

import okhttp3.Request;

public class ShowDetailPresenter extends NetworkHandler {

    private Context context;
    private ShowDetailView showDetailView;

    public ShowDetailPresenter(Context context, ShowDetailView showDetailView) {
        super(context, showDetailView);
        this.context = context;
        this.showDetailView = showDetailView;
    }

    public void callShowsVideoApi(String slug) {
        NetworkManager.getScoopWhoopApi().getShowsVideoList(slug).enqueue(this);
    }

    public void callShowsVideoPagingApi(String slug, String offset) {
        NetworkManager.getScoopWhoopApi().getShowsVideoPagingList(slug, offset).enqueue(this);
    }

    public void callScoopWhoopShowVideoApi(String slug, String offset) {
        NetworkManager.getScoopWhoopApi().getScoopWhoopShowVideoList(slug, offset).enqueue(this);
    }

    public void callUnFavouriteApi(String slug) {
        HashMap<String, String> headerMap = new HashMap<>();
        headerMap.put("Authorization", "Bearer " + AppPreferences.getInstance(context).getPreferencesString(AppConstants.ACCESS_TOKEN));
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("show", slug);
        NetworkManager.getApi().unfavourite(headerMap, jsonObject).enqueue(this);
    }

    public void callFavouriteApi(String slug) {
        HashMap<String, String> headerMap = new HashMap<>();
        headerMap.put("Authorization", "Bearer " + AppPreferences.getInstance(context).getPreferencesString(AppConstants.ACCESS_TOKEN));
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("show", slug);
        NetworkManager.getApi().favourite(headerMap, jsonObject).enqueue(this);
    }

    public void callUserData() {
        HashMap<String, String> headerMap = new HashMap<>();
        headerMap.put("Authorization", "Bearer " + AppPreferences.getInstance(context).getPreferencesString(AppConstants.ACCESS_TOKEN));
        String id = AppPreferences.getInstance(context).getPreferencesString(AppConstants.UID);
        NetworkManager.getApi().getUserData(headerMap, id).enqueue(this);
    }

    @Override
    public boolean handleError(Request request, Error error) {
        if (showDetailView != null) {
            showDetailView.hideLoader();
            showDetailView.showMessage(error.getMessage());
        }
        return super.handleError(request, error);
    }

    @Override
    public boolean handleFailure(Request request, Exception ex, String message) {
        if (showDetailView != null) {
            showDetailView.hideLoader();
            if (Helper.isContainValue(message)) {
                showDetailView.showMessage(message);
            }
        }
        return super.handleFailure(request, ex, message);
    }

    @Override
    public boolean handleResponse(Request request, BaseResponse response) {
        if (showDetailView != null) {
            showDetailView.hideLoader();
            if (response instanceof ShowsVideoResponse) {
                ShowsVideoResponse showsVideoResponse = (ShowsVideoResponse) response;
                showDetailView.setShowsVideoResponse(showsVideoResponse);
            } else if (response instanceof GetUserResponse) {
                GetUserResponse getUserResponse = (GetUserResponse) response;
                showDetailView.getUserData(getUserResponse);
            } else if (response instanceof FavouriteResponse) {
                FavouriteResponse favouriteResponse = (FavouriteResponse) response;
                showDetailView.setFavResponse(favouriteResponse);
            } else if (response instanceof UnFavouriteResponse) {
                UnFavouriteResponse unFavouriteResponse = (UnFavouriteResponse) response;
                showDetailView.setUnFavResponse(unFavouriteResponse);
            } else if (response instanceof ScoopWhoopShowVideoResponse) {
                ScoopWhoopShowVideoResponse scoopWhoopShowVideoResponse = (ScoopWhoopShowVideoResponse) response;
                showDetailView.setScoopWhoopShowVideoResponse(scoopWhoopShowVideoResponse);
            }
        }
        return true;
    }

    public void onDestroyedView() {
        showDetailView = null;
    }
}