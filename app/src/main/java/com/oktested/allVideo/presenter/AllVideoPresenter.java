package com.oktested.allVideo.presenter;

import android.content.Context;

import com.oktested.allVideo.ui.AllVideoView;
import com.oktested.core.model.BaseResponse;
import com.oktested.core.network.NetworkHandler;
import com.oktested.core.network.NetworkManager;
import com.oktested.home.model.AppExclusiveResponse;
import com.oktested.home.model.MostViewedVideoResponse;
import com.oktested.home.model.ScoopWhoopShowVideoResponse;
import com.oktested.home.model.ScoopWhoopVideoResponse;
import com.oktested.home.model.ShowsVideoResponse;
import com.oktested.home.model.TrendingVideoResponse;
import com.oktested.home.model.VideoListResponse;
import com.oktested.utils.Helper;

import okhttp3.Request;

public class AllVideoPresenter extends NetworkHandler {

    private Context context;
    private AllVideoView allVideoView;

    public AllVideoPresenter(Context context, AllVideoView allVideoView) {
        super(context, allVideoView);
        this.context = context;
        this.allVideoView = allVideoView;
    }

    public void callAppExclusivePagingApi(String offset) {
        NetworkManager.getScoopWhoopApi().getAppExclusiveVideoList(offset).enqueue(this);
    }

    public void callRecentlyAddedPagingApi(String offset) {
        NetworkManager.getScoopWhoopApi().getRecentlyAddedPaginationList(offset).enqueue(this);
    }

    public void callTrendingPagingApi(String offset) {
        NetworkManager.getScoopWhoopApi().getTrendingVideoList(offset).enqueue(this);
    }

    public void callMostViewedPagingApi(String offset) {
        NetworkManager.getScoopWhoopApi().getMostViewedVideoList(offset).enqueue(this);
    }

    public void callShowsVideoPagingApi(String slug, String offset) {
        NetworkManager.getScoopWhoopApi().getShowsVideoPagingList(slug, offset).enqueue(this);
    }

    public void callScoopWhoopVideoPagingApi(String offset) {
        NetworkManager.getScoopWhoopApi().getScoopWhoopVideoList(offset).enqueue(this);
    }

    public void callScoopWhoopShowVideoApi(String slug, String offset) {
        NetworkManager.getScoopWhoopApi().getScoopWhoopShowVideoList(slug, offset).enqueue(this);
    }

    @Override
    public boolean handleError(Request request, Error error) {
        if (allVideoView != null) {
            allVideoView.showMessage(error.getMessage());
        }
        return super.handleError(request, error);
    }

    @Override
    public boolean handleFailure(Request request, Exception ex, String message) {
        if (allVideoView != null) {
            if (Helper.isContainValue(message)) {
                allVideoView.showMessage(message);
            }
        }
        return super.handleFailure(request, ex, message);
    }

    @Override
    public boolean handleResponse(Request request, BaseResponse response) {
        if (allVideoView != null) {
            if (response instanceof AppExclusiveResponse) {
                AppExclusiveResponse appExclusiveResponse = (AppExclusiveResponse) response;
                allVideoView.setAppExclusiveResponse(appExclusiveResponse);
            } else if (response instanceof VideoListResponse) {
                VideoListResponse videoListResponse = (VideoListResponse) response;
                allVideoView.setRecentlyAddedResponse(videoListResponse);
            } else if (response instanceof TrendingVideoResponse) {
                TrendingVideoResponse trendingVideoResponse = (TrendingVideoResponse) response;
                allVideoView.setTrendingVideoResponse(trendingVideoResponse);
            } else if (response instanceof MostViewedVideoResponse) {
                MostViewedVideoResponse mostViewedVideoResponse = (MostViewedVideoResponse) response;
                allVideoView.setMostViewedVideoResponse(mostViewedVideoResponse);
            } else if (response instanceof ShowsVideoResponse) {
                ShowsVideoResponse showsVideoResponse = (ShowsVideoResponse) response;
                allVideoView.setShowsVideoResponse(showsVideoResponse);
            } else if (response instanceof ScoopWhoopVideoResponse) {
                ScoopWhoopVideoResponse scoopWhoopVideoResponse = (ScoopWhoopVideoResponse) response;
                allVideoView.setScoopWhoopVideoResponse(scoopWhoopVideoResponse);
            } else if (response instanceof ScoopWhoopShowVideoResponse) {
                ScoopWhoopShowVideoResponse scoopWhoopShowVideoResponse = (ScoopWhoopShowVideoResponse) response;
                allVideoView.setScoopWhoopShowVideoResponse(scoopWhoopShowVideoResponse);
            }
        }
        return true;
    }

    public void onDestroyedView() {
        allVideoView = null;
    }
}