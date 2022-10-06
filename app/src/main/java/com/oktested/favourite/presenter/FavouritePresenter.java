package com.oktested.favourite.presenter;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.oktested.core.model.BaseResponse;
import com.oktested.core.network.NetworkHandler;
import com.oktested.core.network.NetworkManager;
import com.oktested.favourite.ui.FavouriteView;
import com.oktested.home.model.AnchorsResponse;
import com.oktested.home.model.ShowsResponse;
import com.oktested.home.model.VideoListResponse;
import com.oktested.utils.Helper;

import java.util.List;

import okhttp3.Request;

public class FavouritePresenter extends NetworkHandler {

    private FavouriteView favouriteView;

    public FavouritePresenter(Context context, FavouriteView favouriteView) {
        super(context, favouriteView);
        this.favouriteView = favouriteView;
    }

    public void callFavouriteVideoListApi(List<String> videoList) {
        Gson gson = new GsonBuilder().create();
        JsonArray myCustomArray = gson.toJsonTree(videoList).getAsJsonArray();
        JsonObject jsonObject = new JsonObject();
        jsonObject.add("videos", myCustomArray);
        NetworkManager.getScoopWhoopApi().getFavouriteVideoList(jsonObject.toString()).enqueue(this);
    }

    public void callFavouriteShowsListApi(List<String> videoList) {
        Gson gson = new GsonBuilder().create();
        JsonArray myCustomArray = gson.toJsonTree(videoList).getAsJsonArray();
        JsonObject jsonObject = new JsonObject();
        jsonObject.add("shows", myCustomArray);
        NetworkManager.getScoopWhoopActorApi().getFavouriteShowsList(jsonObject.toString()).enqueue(this);
    }

    public void callFavouriteAnchorsListApi(List<String> actorList) {
        Gson gson = new GsonBuilder().create();
        JsonArray myCustomArray = gson.toJsonTree(actorList).getAsJsonArray();
        JsonObject jsonObject = new JsonObject();
        jsonObject.add("castcrews", myCustomArray);
        NetworkManager.getScoopWhoopActorApi().getFavouriteAnchorList(jsonObject.toString()).enqueue(this);
    }

    @Override
    public boolean handleResponse(Request request, BaseResponse response) {
        if (favouriteView != null) {
            if (response instanceof VideoListResponse) {
                VideoListResponse videoListResponse = (VideoListResponse) response;
                favouriteView.getFavouriteVideos(videoListResponse);
            } else if (response instanceof ShowsResponse) {
                ShowsResponse showsResponse = (ShowsResponse) response;
                favouriteView.getFavouriteShows(showsResponse);
            } else if (response instanceof AnchorsResponse) {
                AnchorsResponse anchorDetailResponse = (AnchorsResponse) response;
                favouriteView.getFavouriteAnchors(anchorDetailResponse);
            }
        }
        return true;
    }

    @Override
    public boolean handleError(Request request, Error error) {
        if (favouriteView != null) {
            favouriteView.showMessage(error.getMessage());
        }
        return super.handleError(request, error);
    }

    @Override
    public boolean handleFailure(Request request, Exception ex, String message) {
        if (favouriteView != null) {
            if (Helper.isContainValue(message)) {
                favouriteView.showMessage(message);
            }
        }
        return super.handleFailure(request, ex, message);
    }

    public void onDestroyedView() {
        favouriteView = null;
    }
}