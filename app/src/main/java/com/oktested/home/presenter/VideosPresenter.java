package com.oktested.home.presenter;

import android.content.Context;

import com.oktested.core.model.BaseResponse;
import com.oktested.core.network.NetworkHandler;
import com.oktested.core.network.NetworkManager;
import com.oktested.home.model.AnchorsResponse;
import com.oktested.home.model.AppExclusiveResponse;
import com.oktested.home.model.EditorPickResponse;
import com.oktested.home.model.HomeStructureResponse;
import com.oktested.home.model.MostViewedVideoResponse;
import com.oktested.home.model.ScoopWhoopShowVideoResponse;
import com.oktested.home.model.ScoopWhoopShowsResponse;
import com.oktested.home.model.ScoopWhoopVideoResponse;
import com.oktested.home.model.ShowsResponse;
import com.oktested.home.model.ShowsVideoResponse;
import com.oktested.home.model.TrendingVideoResponse;
import com.oktested.home.model.VideoListResponse;
import com.oktested.home.ui.VideosView;
import com.oktested.utils.Helper;

import okhttp3.Request;

public class VideosPresenter extends NetworkHandler {

    private VideosView videosView;
    private Context context;

    public VideosPresenter(Context context, VideosView videosView) {
        super(context, videosView);
        this.videosView = videosView;
        this.context = context;
    }

    public void callHomeStructure() {
       NetworkManager.getDynamicHomeApi().getHomeStructure().enqueue(this);
    }

    public void callEditorPickApi() {
      NetworkManager.getScoopWhoopApi().getEditorPickVideo().enqueue(this);
    }

    public void callAppExclusiveApi(String offset) {
       NetworkManager.getScoopWhoopApi().getAppExclusiveVideoList(offset).enqueue(this);
    }

    public void callRecentlyAddedApi() {
       NetworkManager.getScoopWhoopApi().getRecentlyAddedVideoList().enqueue(this);
    }

    public void callTrendingApi(String offset) {
        NetworkManager.getScoopWhoopApi().getTrendingVideoList(offset).enqueue(this);
    }

    public void callMostViewedApi(String offset) {
        NetworkManager.getScoopWhoopApi().getMostViewedVideoList(offset).enqueue(this);
    }

    public void callShowsApi(String offset) {
        NetworkManager.getScoopWhoopOldApi().getShowsList(offset).enqueue(this);
    }

    public void callAnchorsApi() {
        NetworkManager.getScoopWhoopOldApi().getAnchorsList().enqueue(this);
    }

    public void callShowsVideoApi(String slug) {
        NetworkManager.getScoopWhoopApi().getShowsVideoList(slug).enqueue(this);
    }

    public void callScoopWhoopVideoApi(String offset) {
        NetworkManager.getScoopWhoopApi().getScoopWhoopVideoList(offset).enqueue(this);
    }

    public void callScoopWhoopShowsApi(String offset) {
        NetworkManager.getScoopWhoopShowsApi().getScoopWhoopShowsList(offset).enqueue(this);
    }

    public void callScoopWhoopShowVideoApi(String slug, String offset) {
        NetworkManager.getScoopWhoopApi().getScoopWhoopShowVideoList(slug, offset).enqueue(this);
    }

    @Override
    public boolean handleFailure(Request request, Exception ex, String message) {
        if (videosView != null) {
            videosView.hideLoader();
            if (Helper.isContainValue(message)) {
                videosView.showMessage(message);
            }
        }
        return super.handleFailure(request, ex, message);
    }

    @Override
    public boolean handleError(Request request, Error error) {
        if (videosView != null) {
            videosView.hideLoader();
            videosView.showMessage(error.getMessage());
        }
        return super.handleError(request, error);
    }

    @Override
    public boolean handleResponse(Request request, BaseResponse response) {
        if (videosView != null) {
            if (response instanceof HomeStructureResponse) {
                HomeStructureResponse homeStructureResponse = (HomeStructureResponse) response;
                videosView.setHomeStructureResponse(homeStructureResponse);
            } else if (response instanceof EditorPickResponse) {
                EditorPickResponse editorPickResponse = (EditorPickResponse) response;
                videosView.setEditorPickResponse(editorPickResponse);
            } else if (response instanceof AppExclusiveResponse) {
                AppExclusiveResponse appExclusiveResponse = (AppExclusiveResponse) response;
                videosView.setAppExclusiveResponse(appExclusiveResponse);
            } else if (response instanceof VideoListResponse) {
                VideoListResponse videoListResponse = (VideoListResponse) response;
                videosView.setRecentlyAddedResponse(videoListResponse);
            } else if (response instanceof TrendingVideoResponse) {
                TrendingVideoResponse trendingVideoResponse = (TrendingVideoResponse) response;
                videosView.setTrendingVideoResponse(trendingVideoResponse);
            } else if (response instanceof MostViewedVideoResponse) {
                MostViewedVideoResponse mostViewedVideoResponse = (MostViewedVideoResponse) response;
                videosView.setMostViewedVideoResponse(mostViewedVideoResponse);
            } else if (response instanceof ShowsResponse) {
                ShowsResponse showsResponse = (ShowsResponse) response;
                videosView.setShowsResponse(showsResponse);
            } else if (response instanceof AnchorsResponse) {
                AnchorsResponse anchorsResponse = (AnchorsResponse) response;
                videosView.setAnchorsResponse(anchorsResponse);
            } else if (response instanceof ShowsVideoResponse) {
                ShowsVideoResponse showsVideoResponse = (ShowsVideoResponse) response;
                videosView.setShowsVideoResponse(showsVideoResponse);
            } else if (response instanceof ScoopWhoopVideoResponse) {
                ScoopWhoopVideoResponse scoopWhoopVideoResponse = (ScoopWhoopVideoResponse) response;
                videosView.setScoopWhoopVideoResponse(scoopWhoopVideoResponse);
            } else if (response instanceof ScoopWhoopShowsResponse) {
                ScoopWhoopShowsResponse scoopWhoopShowsResponse = (ScoopWhoopShowsResponse) response;
                videosView.setScoopWhoopShowsResponse(scoopWhoopShowsResponse);
            } else if (response instanceof ScoopWhoopShowVideoResponse) {
                ScoopWhoopShowVideoResponse scoopWhoopShowVideoResponse = (ScoopWhoopShowVideoResponse) response;
                videosView.setScoopWhoopShowVideoResponse(scoopWhoopShowVideoResponse);
            }
        }
        return true;
    }

    public void onDestroyedView() {
        videosView = null;
    }
}