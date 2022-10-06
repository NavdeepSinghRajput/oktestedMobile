package com.oktested.pickQuiz.presenter;

import android.content.Context;

import com.google.gson.JsonObject;
import com.oktested.core.model.BaseResponse;
import com.oktested.core.network.NetworkHandler;
import com.oktested.core.network.NetworkManager;
import com.oktested.home.model.CategoryDetailQuizResponse;
import com.oktested.home.model.CategoryQuizResponse;
import com.oktested.home.model.LatestQuizResponse;
import com.oktested.home.model.SearchQuizResponse;
import com.oktested.home.model.TrendingQuizResponse;
import com.oktested.pickQuiz.model.SelectQuizResponse;
import com.oktested.pickQuiz.model.SuggestQuizResponse;
import com.oktested.pickQuiz.ui.PickQuizDetailView;
import com.oktested.pickQuiz.ui.PickQuizView;
import com.oktested.quizDetail.model.QuizDetailResponse;
import com.oktested.utils.AppConstants;
import com.oktested.utils.AppPreferences;
import com.oktested.utils.Helper;

import java.util.HashMap;

import okhttp3.Request;

public class PickQuizDetailPresenter extends NetworkHandler {

    private Context context;
    private PickQuizDetailView pickQuizDetailView;

    public PickQuizDetailPresenter(Context context, PickQuizDetailView pickQuizView) {
        super(context, pickQuizView);
        this.context = context;
        this.pickQuizDetailView = pickQuizView;
    }

    public void callQuizDetailApi(String post_id) {
        NetworkManager.getOkTestedQuizApi().getQuizDetail(post_id).enqueue(this);
    }

    public void callSelectQuizApi(String quizId, String channelName) {
        HashMap<String, String> headerMap = new HashMap<>();
        headerMap.put("Authorization", "Bearer " + AppPreferences.getInstance(context).getPreferencesString(AppConstants.ACCESS_TOKEN));
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("quiz_id", quizId);
        jsonObject.addProperty("channel_id", channelName);
        NetworkManager.getApi().selectQuiz(headerMap, jsonObject).enqueue(this);
    }

    public void callSuggestQuizApi(String quizId, String channelName) {
        HashMap<String, String> headerMap = new HashMap<>();
        headerMap.put("Authorization", "Bearer " + AppPreferences.getInstance(context).getPreferencesString(AppConstants.ACCESS_TOKEN));
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("quiz_id", quizId);
        jsonObject.addProperty("channel_id", channelName);
        NetworkManager.getApi().suggestQuiz(headerMap, jsonObject).enqueue(this);
    }

    @Override
    public boolean handleError(Request request, Error error) {
        if (pickQuizDetailView != null) {
            pickQuizDetailView.hideLoader();
            pickQuizDetailView.showMessage(error.getMessage());
        }
        return super.handleError(request, error);
    }

    @Override
    public boolean handleFailure(Request request, Exception ex, String message) {
        if (pickQuizDetailView != null) {
            pickQuizDetailView.hideLoader();
            if (Helper.isContainValue(message)) {
                pickQuizDetailView.showMessage(message);
            }
        }
        return super.handleFailure(request, ex, message);
    }

    @Override
    public boolean handleResponse(Request request, BaseResponse response) {
        if (pickQuizDetailView != null) {
            pickQuizDetailView.hideLoader();
            if (response instanceof QuizDetailResponse) {
                QuizDetailResponse quizDetailModel = (QuizDetailResponse) response;
                pickQuizDetailView.setQuizDetailResponse(quizDetailModel);
            } else if (response instanceof SelectQuizResponse) {
                SelectQuizResponse selectQuizResponse = (SelectQuizResponse) response;
                pickQuizDetailView.selectQuizResponse(selectQuizResponse);
            } else if (response instanceof SuggestQuizResponse) {
                SuggestQuizResponse suggestQuizResponse = (SuggestQuizResponse) response;
                pickQuizDetailView.suggestQuizResponse(suggestQuizResponse);
            }
        }
        return true;
    }

    public void onDestroyedView() {
        pickQuizDetailView = null;
    }
}