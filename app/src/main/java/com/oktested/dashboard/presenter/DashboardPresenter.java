package com.oktested.dashboard.presenter;

import android.content.Context;

import com.google.gson.JsonObject;
import com.oktested.core.model.BaseResponse;
import com.oktested.core.network.NetworkHandler;
import com.oktested.core.network.NetworkManager;
import com.oktested.dashboard.model.GetUserResponse;
import com.oktested.dashboard.ui.DashboardView;
import com.oktested.home.model.NotifyInviteFriendResponse;
import com.oktested.userDetail.model.UpdateUserResponse;
import com.oktested.utils.AppConstants;
import com.oktested.utils.AppPreferences;
import com.oktested.utils.Helper;

import java.util.HashMap;

import okhttp3.Request;

public class DashboardPresenter extends NetworkHandler {

    private Context context;
    private DashboardView dashboardView;

    public DashboardPresenter(Context context, DashboardView dashboardView) {
        super(context, dashboardView);
        this.context = context;
        this.dashboardView = dashboardView;
    }

    public void callUserData() {
        HashMap<String, String> headerMap = new HashMap<>();
        headerMap.put("Authorization", "Bearer " + AppPreferences.getInstance(context).getPreferencesString(AppConstants.ACCESS_TOKEN));
        String id = AppPreferences.getInstance(context).getPreferencesString(AppConstants.UID);
        NetworkManager.getApi().getUserData(headerMap, id).enqueue(this);
    }

    public void callUpdateUserApi() {
        HashMap<String, String> headerMap = new HashMap<>();
        headerMap.put("Authorization", "Bearer " + AppPreferences.getInstance(context).getPreferencesString(AppConstants.ACCESS_TOKEN));
        JsonObject jsonObject = new JsonObject();
        if (Helper.isContainValue(AppPreferences.getInstance(context).getPreferencesString(AppConstants.FIREBASE_TOKEN))) {
            jsonObject.addProperty("firebase_token", AppPreferences.getInstance(context).getPreferencesString(AppConstants.FIREBASE_TOKEN));
        }
        String id = AppPreferences.getInstance(context).getPreferencesString(AppConstants.UID);
        NetworkManager.getApi().updateUserData(headerMap, jsonObject, id).enqueue(this);
    }

    public void notifyInviteLink(String urlReffer) {
        HashMap<String, String> headerMap = new HashMap<>();
        headerMap.put("Authorization", "Bearer " + AppPreferences.getInstance(context).getPreferencesString(AppConstants.ACCESS_TOKEN));
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("url", urlReffer);
        NetworkManager.getApi().notifyInviteLinkRequest(headerMap, jsonObject).enqueue(this);
    }

    @Override
    public boolean handleError(Request request, Error error) {
        if (dashboardView != null) {
            dashboardView.hideLoader();
            dashboardView.showMessage(error.getMessage());
        }
        return super.handleError(request, error);
    }

    @Override
    public boolean handleFailure(Request request, Exception ex, String message) {
        if (dashboardView != null) {
            dashboardView.hideLoader();
            if (Helper.isContainValue(message)) {
                dashboardView.showMessage(message);
            }
        }
        return super.handleFailure(request, ex, message);
    }

    @Override
    public boolean handleResponse(Request request, BaseResponse response) {
        if (dashboardView != null) {
            dashboardView.hideLoader();
            if (response instanceof GetUserResponse) {
                GetUserResponse getUserResponse = (GetUserResponse) response;
                dashboardView.getUserData(getUserResponse);
            } else if (response instanceof UpdateUserResponse) {
                UpdateUserResponse updateUserResponse = (UpdateUserResponse) response;
                dashboardView.setUpdateData(updateUserResponse);
            } else if (response instanceof NotifyInviteFriendResponse) {
                NotifyInviteFriendResponse notifyInviteFriendResponse = (NotifyInviteFriendResponse) response;
                dashboardView.setNotifyInviteLink(notifyInviteFriendResponse);
            }
        }
        return true;
    }

    public void onDestroyedView() {
        dashboardView = null;
    }
}