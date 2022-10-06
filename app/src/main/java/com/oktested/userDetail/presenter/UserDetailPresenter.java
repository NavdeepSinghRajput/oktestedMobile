package com.oktested.userDetail.presenter;

import android.content.Context;

import com.google.gson.JsonObject;
import com.oktested.core.model.BaseResponse;
import com.oktested.core.network.NetworkHandler;
import com.oktested.core.network.NetworkManager;
import com.oktested.userDetail.model.UpdateUserResponse;
import com.oktested.userDetail.ui.UserDetailView;
import com.oktested.utils.AppConstants;
import com.oktested.utils.AppPreferences;
import com.oktested.utils.Helper;

import java.util.HashMap;

import okhttp3.Request;

public class UserDetailPresenter extends NetworkHandler {

    private Context context;
    private UserDetailView userDetailView;

    public UserDetailPresenter(Context context, UserDetailView userDetailView) {
        super(context, userDetailView);
        this.context = context;
        this.userDetailView = userDetailView;
    }

    public void callUpdateUserApi(String gender, String dob) {
        HashMap<String, String> headerMap = new HashMap<>();
        headerMap.put("Authorization", "Bearer " + AppPreferences.getInstance(context).getPreferencesString(AppConstants.ACCESS_TOKEN));
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("gender", gender);
        jsonObject.addProperty("dob", dob);
        if (Helper.isContainValue(AppPreferences.getInstance(context).getPreferencesString(AppConstants.FIREBASE_TOKEN))) {
            jsonObject.addProperty("firebase_token", AppPreferences.getInstance(context).getPreferencesString(AppConstants.FIREBASE_TOKEN));
        }
        String id = AppPreferences.getInstance(context).getPreferencesString(AppConstants.UID);
        NetworkManager.getApi().updateUserData(headerMap, jsonObject, id).enqueue(this);
    }

    public void callUpdateUserWithNameApi(String gender, String dob, String name) {
        HashMap<String, String> headerMap = new HashMap<>();
        headerMap.put("Authorization", "Bearer " + AppPreferences.getInstance(context).getPreferencesString(AppConstants.ACCESS_TOKEN));
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("gender", gender);
        jsonObject.addProperty("dob", dob);
        jsonObject.addProperty("display_name", name);
        if (Helper.isContainValue(AppPreferences.getInstance(context).getPreferencesString(AppConstants.FIREBASE_TOKEN))) {
            jsonObject.addProperty("firebase_token", AppPreferences.getInstance(context).getPreferencesString(AppConstants.FIREBASE_TOKEN));
        }
        String id = AppPreferences.getInstance(context).getPreferencesString(AppConstants.UID);
        NetworkManager.getApi().updateUserData(headerMap, jsonObject, id).enqueue(this);
    }

    public void callUpdateNameApi(String name) {
        HashMap<String, String> headerMap = new HashMap<>();
        headerMap.put("Authorization", "Bearer " + AppPreferences.getInstance(context).getPreferencesString(AppConstants.ACCESS_TOKEN));
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("display_name", name);
        if (Helper.isContainValue(AppPreferences.getInstance(context).getPreferencesString(AppConstants.FIREBASE_TOKEN))) {
            jsonObject.addProperty("firebase_token", AppPreferences.getInstance(context).getPreferencesString(AppConstants.FIREBASE_TOKEN));
        }
        String id = AppPreferences.getInstance(context).getPreferencesString(AppConstants.UID);
        NetworkManager.getApi().updateUserData(headerMap, jsonObject, id).enqueue(this);
    }

    @Override
    public boolean handleResponse(Request request, BaseResponse response) {
        if (userDetailView != null && response != null) {
            userDetailView.hideLoader();
            if (response instanceof UpdateUserResponse) {
                UpdateUserResponse updateUserResponse = (UpdateUserResponse) response;
                userDetailView.setUpdateData(updateUserResponse);
            }
        }
        return true;
    }

    @Override
    public boolean handleError(Request request, Error error) {
        if (userDetailView != null) {
            userDetailView.hideLoader();
            userDetailView.showMessage(error.getMessage());
        }
        return super.handleError(request, error);
    }

    @Override
    public boolean handleFailure(Request request, Exception ex, String message) {
        if (userDetailView != null) {
            userDetailView.hideLoader();
            if (Helper.isContainValue(message)) {
                userDetailView.showMessage(message);
            }
        }
        return super.handleFailure(request, ex, message);
    }

    public void onDestroyedView() {
        userDetailView = null;
    }
}