package com.oktested.quizProfile.presenter;

import android.content.Context;

import com.google.gson.JsonObject;
import com.oktested.core.model.BaseResponse;
import com.oktested.core.network.NetworkHandler;
import com.oktested.core.network.NetworkManager;
import com.oktested.quizProfile.model.RecentPlayedQuizResponse;
import com.oktested.quizProfile.model.UserStatsResponse;
import com.oktested.quizProfile.ui.QuizProfileView;
import com.oktested.utils.AppConstants;
import com.oktested.utils.AppPreferences;
import com.oktested.utils.Helper;

import java.io.File;
import java.util.HashMap;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;

public class QuizProfilePresenter extends NetworkHandler {

    private Context context;
    private QuizProfileView quizProfileView;
    private int position;
    private String mobileNumber;

    public QuizProfilePresenter(Context context, QuizProfileView quizProfileView) {
        super(context, quizProfileView);
        this.context = context;
        this.quizProfileView = quizProfileView;
    }

    @Override
    public boolean handleError(Request request, Error error) {
        if (quizProfileView != null) {
            quizProfileView.hideLoader();
            quizProfileView.showMessage(error.getMessage());
        }
        return super.handleError(request, error);
    }

    @Override
    public boolean handleFailure(Request request, Exception ex, String message) {
        if (quizProfileView != null) {
            quizProfileView.hideLoader();
            if (Helper.isContainValue(message)) {
                quizProfileView.showMessage(message);
            }
        }
        return super.handleFailure(request, ex, message);
    }

    @Override
    public boolean handleResponse(Request request, BaseResponse response) {
        if (quizProfileView != null) {
            if (response instanceof RecentPlayedQuizResponse) {
                RecentPlayedQuizResponse recentPlayedQuizResponse = (RecentPlayedQuizResponse) response;
                quizProfileView.setRecentPlayedQuizResponse(recentPlayedQuizResponse);
            } else if (response instanceof UserStatsResponse) {
                UserStatsResponse userStatsResponse = (UserStatsResponse) response;
                quizProfileView.setUserStatsResponse(userStatsResponse);
            }
            quizProfileView.hideLoader();
        }
        return true;
    }

    public void onDestroyedView() {
        quizProfileView = null;
    }

    public void callRecentPlayedQuizApi(String offset) {
        HashMap<String, String> headerMap = new HashMap<>();
        headerMap.put("Authorization", "Bearer " + AppPreferences.getInstance(context).getPreferencesString(AppConstants.ACCESS_TOKEN));
        NetworkManager.getApi().getRecentPlayedQuiz(headerMap, offset).enqueue(this);
    }

    public void callUserStatsApi() {
        HashMap<String, String> headerMap = new HashMap<>();
        headerMap.put("Authorization", "Bearer " + AppPreferences.getInstance(context).getPreferencesString(AppConstants.ACCESS_TOKEN));
        NetworkManager.getApi().getUserStats(headerMap).enqueue(this);
    }
}