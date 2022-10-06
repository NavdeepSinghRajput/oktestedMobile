package com.oktested.login.presenter;

import android.content.Context;
import android.provider.Settings;

import com.google.gson.JsonObject;
import com.oktested.core.model.BaseResponse;
import com.oktested.core.network.NetworkHandler;
import com.oktested.core.network.NetworkManager;
import com.oktested.login.model.CreateUserResponse;
import com.oktested.login.model.SocialLoginResponse;
import com.oktested.login.ui.LoginView;
import com.oktested.utils.AppConstants;
import com.oktested.utils.AppPreferences;
import com.oktested.utils.Helper;

import okhttp3.Request;

public class LoginPresenter extends NetworkHandler {

    private LoginView loginView;
    private Context context;

    public LoginPresenter(Context context, LoginView loginView) {
        super(context, loginView);
        this.loginView = loginView;
        this.context = context;
    }

    @Override
    public boolean handleFailure(Request request, Exception ex, String message) {
        if (loginView != null) {
            loginView.hideLoader();
            if (Helper.isContainValue(message)) {
                loginView.showMessage(message);
            }
        }
        return super.handleFailure(request, ex, message);
    }

    @Override
    public boolean handleError(Request request, Error error) {
        if (loginView != null) {
            loginView.hideLoader();
            loginView.showMessage(error.getMessage());
        }
        return super.handleError(request, error);
    }

    @Override
    public boolean handleResponse(Request request, BaseResponse response) {
        if (loginView != null && response != null) {
            loginView.hideLoader();
            if (response.status.equalsIgnoreCase("fail")) {
                loginView.showMessage(response.message);
            } else {
                if (response instanceof CreateUserResponse) {
                    CreateUserResponse createUserResponse = (CreateUserResponse) response;
                    if (createUserResponse.status.equalsIgnoreCase("success")) {
                        loginView.onResponse(createUserResponse);
                    } else {
                        loginView.showMessage(createUserResponse.message);
                    }
                } else if (response instanceof SocialLoginResponse) {
                    SocialLoginResponse socialLoginResponse = (SocialLoginResponse) response;
                    loginView.socialLoginResponse(socialLoginResponse);
                }
            }
        }
        return true;
    }

    public void getSocialLogin(String type, String name, String email, String id, String token) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("source", type);
        jsonObject.addProperty("name", name);
        jsonObject.addProperty("email", email);
        jsonObject.addProperty("user_id", id);
        if (Helper.isContainValue(AppPreferences.getInstance(context).getPreferencesString(AppConstants.FIREBASE_TOKEN))) {
            jsonObject.addProperty("firebase_token", AppPreferences.getInstance(context).getPreferencesString(AppConstants.FIREBASE_TOKEN));
        }
       /* jsonObject.addProperty("image", base64);
        jsonObject.addProperty("image_name", AppConstant.IMAGE_NAME);*/
        jsonObject.addProperty("access_token", token);
        jsonObject.addProperty("device_id", Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID));
        jsonObject.addProperty("device_type", "Android");
        NetworkManager.getApi().socialLogin(jsonObject).enqueue(this);
    }

    public void createUser(String mobileNumber) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("mobile", "+91" + mobileNumber);
        NetworkManager.getApi().createUser(jsonObject).enqueue(this);
    }

    public void onDestroyedView() {
        loginView = null;
    }
}