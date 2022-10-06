package com.oktested.reportProblem.presenter;

import android.content.Context;

import com.google.gson.JsonObject;
import com.oktested.core.model.BaseResponse;
import com.oktested.core.network.NetworkHandler;
import com.oktested.core.network.NetworkManager;
import com.oktested.reportProblem.model.ReportProblemResponse;
import com.oktested.reportProblem.ui.ReportProblemView;
import com.oktested.utils.AppConstants;
import com.oktested.utils.AppPreferences;
import com.oktested.utils.Helper;

import java.util.HashMap;

import okhttp3.Request;

public class ReportProblemPresenter extends NetworkHandler {

    private Context context;
    private ReportProblemView reportProblemView;

    public ReportProblemPresenter(Context context, ReportProblemView reportProblemView) {
        super(context, reportProblemView);
        this.context = context;
        this.reportProblemView = reportProblemView;
    }

    public void callReportApi(String text) {
        HashMap<String, String> headerMap = new HashMap<>();
        headerMap.put("Authorization", "Bearer " + AppPreferences.getInstance(context).getPreferencesString(AppConstants.ACCESS_TOKEN));
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("content", text);
        NetworkManager.getApi().reportProblem(headerMap, jsonObject).enqueue(this);
    }

    @Override
    public boolean handleResponse(Request request, BaseResponse response) {
        if (reportProblemView != null) {
            reportProblemView.hideLoader();
            if (response instanceof ReportProblemResponse) {
                ReportProblemResponse reportProblemResponse = (ReportProblemResponse) response;
                reportProblemView.showResponse(reportProblemResponse);
            }
        }
        return true;
    }

    @Override
    public boolean handleFailure(Request request, Exception ex, String message) {
        if (reportProblemView != null) {
            reportProblemView.hideLoader();
            if (Helper.isContainValue(message)) {
                reportProblemView.showMessage(message);
            }
        }
        return super.handleFailure(request, ex, message);
    }

    @Override
    public boolean handleError(Request request, Error error) {
        if (reportProblemView != null) {
            reportProblemView.hideLoader();
            reportProblemView.showMessage(error.getMessage());
        }
        return super.handleError(request, error);
    }

    public void onDestroyedView() {
        reportProblemView = null;
    }
}