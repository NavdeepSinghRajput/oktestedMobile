package com.oktested.verifyotp.presenter;

import android.content.Context;
import android.provider.Settings;

import com.google.gson.JsonObject;
import com.oktested.core.model.BaseResponse;
import com.oktested.core.network.NetworkHandler;
import com.oktested.core.network.NetworkManager;
import com.oktested.utils.AppConstants;
import com.oktested.utils.AppPreferences;
import com.oktested.utils.Helper;
import com.oktested.verifyotp.model.ResendOtpResponse;
import com.oktested.verifyotp.model.VerifyOtpResponse;
import com.oktested.verifyotp.ui.VerifyOtpView;

import java.util.HashMap;

import okhttp3.Request;

public class VerifyOtpPresenter extends NetworkHandler {

    private VerifyOtpView verifyOtpView;
    private Context context;

    public VerifyOtpPresenter(Context context, VerifyOtpView verifyOtpView) {
        super(context, verifyOtpView);
        this.verifyOtpView = verifyOtpView;
        this.context = context;
    }

    public void callVerifyOtp(String otp, String token) {
        HashMap<String, String> headerMap = new HashMap<>();
        headerMap.put("Authorization", "Bearer " + token);
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("otp", otp);
        if (Helper.isContainValue(AppPreferences.getInstance(context).getPreferencesString(AppConstants.FIREBASE_TOKEN))) {
            jsonObject.addProperty("firebase_token", AppPreferences.getInstance(context).getPreferencesString(AppConstants.FIREBASE_TOKEN));
        }
        jsonObject.addProperty("device_id", Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID));
        jsonObject.addProperty("device_type", "Android");
        NetworkManager.getApi().verifyOtp(headerMap, jsonObject).enqueue(this);
    }

    public void callResendOtpApi(String mobile) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("mobile", mobile);
        NetworkManager.getApi().resendOtp(jsonObject).enqueue(this);
    }

    @Override
    public boolean handleResponse(Request request, BaseResponse response) {
        if (verifyOtpView != null) {
            verifyOtpView.hideLoader();
            if (response instanceof ResendOtpResponse) {
                ResendOtpResponse resendOtpResponse = (ResendOtpResponse) response;
                if (resendOtpResponse.message != null) {
                    verifyOtpView.resendOtpResponse(resendOtpResponse);
                }
            } else if (response instanceof VerifyOtpResponse) {
                VerifyOtpResponse verifyOtpResponse = (VerifyOtpResponse) response;
                if (!response.status.equalsIgnoreCase("fail")) {
                    verifyOtpView.verifyOtpResponse(verifyOtpResponse);
                } else {
                    verifyOtpView.showMessage(response.message);
                }
            }
        }
        return true;
    }

    @Override
    public boolean handleError(Request request, Error error) {
        if (verifyOtpView != null) {
            verifyOtpView.hideLoader();
            verifyOtpView.showMessage(error.getMessage());
        }
        return super.handleError(request, error);
    }

    @Override
    public boolean handleFailure(Request request, Exception ex, String message) {
        if (verifyOtpView != null) {
            verifyOtpView.hideLoader();
            if (Helper.isContainValue(message)) {
                verifyOtpView.showMessage(message);
            }
        }
        return super.handleFailure(request, ex, message);
    }

    public void onDestroyedView() {
        verifyOtpView = null;
    }
}