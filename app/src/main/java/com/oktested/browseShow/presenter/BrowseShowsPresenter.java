package com.oktested.browseShow.presenter;

import android.content.Context;

import com.oktested.browseShow.ui.BrowseShowsView;
import com.oktested.core.model.BaseResponse;
import com.oktested.core.network.NetworkHandler;
import com.oktested.core.network.NetworkManager;
import com.oktested.home.model.ScoopWhoopShowsResponse;
import com.oktested.home.model.ShowsResponse;
import com.oktested.utils.Helper;

import okhttp3.Request;

public class BrowseShowsPresenter extends NetworkHandler {

    private BrowseShowsView browseShowsView;
    private Context context;

    public BrowseShowsPresenter(Context context, BrowseShowsView browseShowsView) {
        super(context, browseShowsView);
        this.browseShowsView = browseShowsView;
        this.context = context;
    }

    public void callShowsApi(String offset) {
        NetworkManager.getScoopWhoopOldApi().getShowsList(offset).enqueue(this);
    }

    public void callScoopWhoopShowsApi(String offset) {
        NetworkManager.getScoopWhoopShowsApi().getScoopWhoopShowsList(offset).enqueue(this);
    }

    @Override
    public boolean handleError(Request request, Error error) {
        if (browseShowsView != null) {
            browseShowsView.hideLoader();
            browseShowsView.showMessage(error.getMessage());
        }
        return super.handleError(request, error);
    }

    @Override
    public boolean handleFailure(Request request, Exception ex, String message) {
        if (browseShowsView != null) {
            browseShowsView.hideLoader();
            if (Helper.isContainValue(message)) {
                browseShowsView.showMessage(message);
            }
        }
        return super.handleFailure(request, ex, message);
    }

    @Override
    public boolean handleResponse(Request request, BaseResponse response) {
        if (browseShowsView != null) {
            browseShowsView.hideLoader();
            if (response instanceof ShowsResponse) {
                ShowsResponse showsResponse = (ShowsResponse) response;
                browseShowsView.setShowsResponse(showsResponse);
            } else if (response instanceof ScoopWhoopShowsResponse) {
                ScoopWhoopShowsResponse scoopWhoopShowsResponse = (ScoopWhoopShowsResponse) response;
                browseShowsView.setScoopWhoopShowsResponse(scoopWhoopShowsResponse);
            }
        }
        return true;
    }

    public void onDestroyedView() {
        browseShowsView = null;
    }
}