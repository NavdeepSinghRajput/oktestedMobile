package com.oktested.browseAnchor.presenter;

import android.content.Context;

import com.oktested.browseAnchor.ui.BrowseAnchorView;
import com.oktested.core.model.BaseResponse;
import com.oktested.core.network.NetworkHandler;
import com.oktested.core.network.NetworkManager;
import com.oktested.home.model.AnchorsResponse;
import com.oktested.utils.Helper;

import okhttp3.Request;

public class BrowseAnchorPresenter extends NetworkHandler {

    private BrowseAnchorView browseAnchorView;
    private Context context;

    public BrowseAnchorPresenter(Context context, BrowseAnchorView browseAnchorView) {
        super(context, browseAnchorView);
        this.context = context;
        this.browseAnchorView = browseAnchorView;
    }

    public void callAnchorsApi() {
        NetworkManager.getScoopWhoopOldApi().getAnchorsList().enqueue(this);
    }

    @Override
    public boolean handleResponse(Request request, BaseResponse response) {
        if (browseAnchorView != null) {
            browseAnchorView.hideLoader();
            if (response instanceof AnchorsResponse) {
                AnchorsResponse anchorsResponse = (AnchorsResponse) response;
                browseAnchorView.setAnchorsResponse(anchorsResponse);
            }
        }
        return true;
    }

    @Override
    public boolean handleFailure(Request request, Exception ex, String message) {
        if (browseAnchorView != null) {
            browseAnchorView.hideLoader();
            if (Helper.isContainValue(message)) {
                browseAnchorView.showMessage(message);
            }
        }
        return super.handleFailure(request, ex, message);
    }

    @Override
    public boolean handleError(Request request, Error error) {
        if (browseAnchorView != null) {
            browseAnchorView.hideLoader();
            browseAnchorView.showMessage(error.getMessage());
        }
        return super.handleError(request, error);
    }

    public void onDestroyedView() {
        browseAnchorView = null;
    }
}