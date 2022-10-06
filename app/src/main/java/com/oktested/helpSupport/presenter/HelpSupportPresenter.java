package com.oktested.helpSupport.presenter;

import android.content.Context;

import com.oktested.core.model.BaseResponse;
import com.oktested.core.network.NetworkHandler;
import com.oktested.core.network.NetworkManager;
import com.oktested.helpSupport.model.HelpResponse;
import com.oktested.helpSupport.ui.HelpSupportView;
import com.oktested.utils.AppConstants;
import com.oktested.utils.AppPreferences;
import com.oktested.utils.Helper;

import java.util.HashMap;

import okhttp3.Request;

public class HelpSupportPresenter extends NetworkHandler {

    private Context context;
    private HelpSupportView helpSupportView;

    public HelpSupportPresenter(Context context, HelpSupportView helpSupportView) {
        super(context, helpSupportView);
        this.context = context;
        this.helpSupportView = helpSupportView;
    }

    public void callHelpApi() {
        HashMap<String, String> headerMap = new HashMap<>();
        headerMap.put("Authorization", "Bearer " + AppPreferences.getInstance(context).getPreferencesString(AppConstants.ACCESS_TOKEN));
        NetworkManager.getApi().getHelpQuestions(headerMap).enqueue(this);
    }

    @Override
    public boolean handleError(Request request, Error error) {
        if (helpSupportView != null) {
            helpSupportView.hideLoader();
            helpSupportView.showMessage(error.getMessage());
        }
        return super.handleError(request, error);
    }

    @Override
    public boolean handleFailure(Request request, Exception ex, String message) {
        if (helpSupportView != null) {
            helpSupportView.hideLoader();
            if (Helper.isContainValue(message)) {
                helpSupportView.showMessage(message);
            }
        }
        return super.handleFailure(request, ex, message);
    }

    @Override
    public boolean handleResponse(Request request, BaseResponse response) {
        if (helpSupportView != null) {
            helpSupportView.hideLoader();
            if (response instanceof HelpResponse) {
                HelpResponse helpResponse = (HelpResponse) response;
                helpSupportView.setHelpResponse(helpResponse);
            }
        }
        return true;
    }

    public void onDestroyedView() {
        helpSupportView = null;
    }
}