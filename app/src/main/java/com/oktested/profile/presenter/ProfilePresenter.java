package com.oktested.profile.presenter;

import android.content.Context;

import com.google.gson.JsonObject;
import com.oktested.core.model.BaseResponse;
import com.oktested.core.network.NetworkHandler;
import com.oktested.core.network.NetworkManager;
import com.oktested.dashboard.model.GetUserResponse;
import com.oktested.profile.ui.ProfileView;
import com.oktested.userDetail.model.UpdateUserResponse;
import com.oktested.utils.AppConstants;
import com.oktested.utils.AppPreferences;
import com.oktested.utils.Helper;

import java.util.HashMap;

import okhttp3.Request;

public class ProfilePresenter extends NetworkHandler {

    private Context context;
    private ProfileView profileView;

    public ProfilePresenter(Context context, ProfileView profileView) {
        super(context, profileView);
        this.context = context;
        this.profileView = profileView;
    }

    public void callUpdateUserApi(String gender, String dob, String name, String base64Path, String imageName) {
        HashMap<String, String> headerMap = new HashMap<>();
        headerMap.put("Authorization", "Bearer " + AppPreferences.getInstance(context).getPreferencesString(AppConstants.ACCESS_TOKEN));
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("display_name", name);
        jsonObject.addProperty("gender", gender);
        jsonObject.addProperty("image", base64Path);
        jsonObject.addProperty("image_name", imageName);
        jsonObject.addProperty("dob", dob);
        String id = AppPreferences.getInstance(context).getPreferencesString(AppConstants.UID);
        NetworkManager.getApi().updateUserData(headerMap, jsonObject, id).enqueue(this);
    }

    public void getUserData() {
        HashMap<String, String> headerMap = new HashMap<>();
        headerMap.put("Authorization", "Bearer " + AppPreferences.getInstance(context).getPreferencesString(AppConstants.ACCESS_TOKEN));
        String id = AppPreferences.getInstance(context).getPreferencesString(AppConstants.UID);
        NetworkManager.getApi().getUserData(headerMap, id).enqueue(this);
    }

    @Override
    public boolean handleResponse(Request request, BaseResponse response) {
        if (profileView != null && response != null) {
            profileView.hideLoader();
            if (response instanceof UpdateUserResponse) {
                UpdateUserResponse updateUserResponse = (UpdateUserResponse) response;
                profileView.setUpdateData(updateUserResponse);
            } else if (response instanceof GetUserResponse) {
                GetUserResponse getUserResponse = (GetUserResponse) response;
                profileView.getUserData(getUserResponse);
            }
        }
        return true;
    }

    @Override
    public boolean handleError(Request request, Error error) {
        if (profileView != null) {
            profileView.hideLoader();
            profileView.showMessage(error.getMessage());
        }
        return super.handleError(request, error);
    }

    @Override
    public boolean handleFailure(Request request, Exception ex, String message) {
        if (profileView != null) {
            profileView.hideLoader();
            if (Helper.isContainValue(message)) {
                profileView.showMessage(message);
            }
        }
        return super.handleFailure(request, ex, message);
    }

    public void onDestroyedView() {
        profileView = null;
    }
}