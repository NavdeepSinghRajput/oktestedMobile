package com.oktested.updateMobile.presenter;

import android.content.Context;

import com.google.gson.JsonObject;
import com.oktested.core.model.BaseResponse;
import com.oktested.core.network.NetworkHandler;
import com.oktested.core.network.NetworkManager;
import com.oktested.updateMobile.model.VerifyMobileNumberResponse;
import com.oktested.updateMobile.ui.VerifyMobileView;
import com.oktested.utils.AppConstants;
import com.oktested.utils.AppPreferences;
import com.oktested.utils.Helper;
import com.oktested.verifyotp.model.ResendOtpResponse;

import java.util.HashMap;

import okhttp3.Request;

public class VerifyMobilePresenter extends NetworkHandler {

    private Context context;
    private VerifyMobileView verifyMobileView;

    public VerifyMobilePresenter(Context context, VerifyMobileView verifyMobileView) {
        super(context, verifyMobileView);
        this.context = context;
        this.verifyMobileView = verifyMobileView;
    }

    public void verifyMobileNumberApi(String mobileNumber, String otp) {
        HashMap<String, String> headerMap = new HashMap<>();
        headerMap.put("Authorization", "Bearer " + AppPreferences.getInstance(context).getPreferencesString(AppConstants.ACCESS_TOKEN));
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("mobile", "+91" + mobileNumber);
        jsonObject.addProperty("otp", otp);
        NetworkManager.getApi().verifyMobileNumber(headerMap, jsonObject).enqueue(this);
    }

    public void callResendOtpApi(String mobile) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("mobile", mobile);
        NetworkManager.getApi().resendOtp(jsonObject).enqueue(this);
    }

    @Override
    public boolean handleError(Request request, Error error) {
        if (verifyMobileView != null) {
            verifyMobileView.hideLoader();
            verifyMobileView.showMessage(error.getMessage());
        }
        return super.handleError(request, error);
    }

    @Override
    public boolean handleFailure(Request request, Exception ex, String message) {
        if (verifyMobileView != null) {
            verifyMobileView.hideLoader();
            if (Helper.isContainValue(message)) {
                verifyMobileView.showMessage(message);
            }
        }
        return super.handleFailure(request, ex, message);
    }

    @Override
    public boolean handleResponse(Request request, BaseResponse response) {
        if (verifyMobileView != null) {
            verifyMobileView.hideLoader();
            if (response instanceof VerifyMobileNumberResponse) {
                VerifyMobileNumberResponse verifyMobileNumberResponse = (VerifyMobileNumberResponse) response;
                verifyMobileView.verifyMobileNumberResponse(verifyMobileNumberResponse);
            } else if (response instanceof ResendOtpResponse) {
                ResendOtpResponse resendOtpResponse = (ResendOtpResponse) response;
                if (resendOtpResponse.message != null) {
                    verifyMobileView.resendOtpResponse(resendOtpResponse);
                }
            }
        }
        return true;
    }

    public void onDestroyedView() {
        verifyMobileView = null;
    }
}