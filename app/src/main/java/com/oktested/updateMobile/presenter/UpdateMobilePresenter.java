package com.oktested.updateMobile.presenter;

import android.content.Context;

import com.google.gson.JsonObject;
import com.oktested.core.model.BaseResponse;
import com.oktested.core.network.NetworkHandler;
import com.oktested.core.network.NetworkManager;
import com.oktested.updateMobile.model.UpdateMobileNumberResponse;
import com.oktested.updateMobile.ui.UpdateMobileView;
import com.oktested.utils.AppConstants;
import com.oktested.utils.AppPreferences;
import com.oktested.utils.Helper;

import java.util.HashMap;

import okhttp3.Request;

public class UpdateMobilePresenter extends NetworkHandler {

    private Context context;
    private UpdateMobileView updateMobileView;

    public UpdateMobilePresenter(Context context, UpdateMobileView updateMobileView) {
        super(context, updateMobileView);
        this.context = context;
        this.updateMobileView = updateMobileView;
    }

    public void updateMobileNumberApi(String mobileNumber) {
        HashMap<String, String> headerMap = new HashMap<>();
        headerMap.put("Authorization", "Bearer " + AppPreferences.getInstance(context).getPreferencesString(AppConstants.ACCESS_TOKEN));
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("mobile", "+91" + mobileNumber);
        NetworkManager.getApi().updateMobileNumber(headerMap, jsonObject).enqueue(this);
    }

    @Override
    public boolean handleError(Request request, Error error) {
        if (updateMobileView != null) {
            updateMobileView.hideLoader();
            updateMobileView.showMessage(error.getMessage());
        }
        return super.handleError(request, error);
    }

    @Override
    public boolean handleFailure(Request request, Exception ex, String message) {
        if (updateMobileView != null) {
            updateMobileView.hideLoader();
            if (Helper.isContainValue(message)) {
                updateMobileView.showMessage(message);
            }
        }
        return super.handleFailure(request, ex, message);
    }

    @Override
    public boolean handleResponse(Request request, BaseResponse response) {
        if (updateMobileView != null) {
            updateMobileView.hideLoader();
            if (response instanceof UpdateMobileNumberResponse) {
                UpdateMobileNumberResponse updateMobileNumberResponse = (UpdateMobileNumberResponse) response;
                updateMobileView.setUpdateMobileNumberResponse(updateMobileNumberResponse);
            }
        }
        return true;
    }

    public void onDestroyedView() {
        updateMobileView = null;
    }
}