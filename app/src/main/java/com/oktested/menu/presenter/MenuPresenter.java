package com.oktested.menu.presenter;

import android.content.Context;

import com.google.gson.JsonObject;
import com.oktested.core.model.BaseResponse;
import com.oktested.core.network.NetworkHandler;
import com.oktested.core.network.NetworkManager;
import com.oktested.menu.ui.MenuView;
import com.oktested.utils.AppConstants;
import com.oktested.utils.AppPreferences;
import com.oktested.utils.Helper;

import java.util.HashMap;

import okhttp3.Request;

public class MenuPresenter extends NetworkHandler {

    private Context context;
    private MenuView menuView;

    public MenuPresenter(Context context, MenuView menuView) {
        super(context, menuView);
        this.context = context;
        this.menuView = menuView;
    }

    public void callNotificationSubscribe(String type) {
        HashMap<String, String> headerMap = new HashMap<>();
        headerMap.put("Authorization", "Bearer " + AppPreferences.getInstance(context).getPreferencesString(AppConstants.ACCESS_TOKEN));
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("type", type);
        NetworkManager.getApi().subscribeNotification(headerMap, jsonObject).enqueue(this);
    }

    public void callNotificationUnsubscribe(String type) {
        HashMap<String, String> headerMap = new HashMap<>();
        headerMap.put("Authorization", "Bearer " + AppPreferences.getInstance(context).getPreferencesString(AppConstants.ACCESS_TOKEN));
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("type", type);
        NetworkManager.getApi().unsubscribeNotification(headerMap, jsonObject).enqueue(this);
    }

    @Override
    public boolean handleFailure(Request request, Exception ex, String message) {
        if (menuView != null) {
            menuView.hideLoader();
            if (Helper.isContainValue(message)) {
                menuView.showMessage(message);
            }
        }
        return super.handleFailure(request, ex, message);
    }

    @Override
    public boolean handleError(Request request, Error error) {
        if (menuView != null) {
            menuView.hideLoader();
            menuView.showMessage(error.getMessage());
        }
        return super.handleError(request, error);
    }

    @Override
    public boolean handleResponse(Request request, BaseResponse response) {
        if (menuView != null) {
            menuView.hideLoader();
        }
        return true;
    }

    public void onDestroyedView() {
        menuView = null;
    }
}