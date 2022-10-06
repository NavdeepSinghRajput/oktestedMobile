package com.oktested.playSolo.presenter;

import android.content.Context;

import com.google.gson.JsonObject;
import com.oktested.core.model.BaseResponse;
import com.oktested.core.network.NetworkHandler;
import com.oktested.core.network.NetworkManager;
import com.oktested.playSolo.model.InsertQuizQuestionResponse;
import com.oktested.playSolo.model.QuizStartResponse;
import com.oktested.playSolo.ui.PlaySoloView;
import com.oktested.utils.AppConstants;
import com.oktested.utils.AppPreferences;
import com.oktested.utils.Helper;

import java.util.HashMap;

import okhttp3.Request;

public class PlaySoloPresenter extends NetworkHandler {

    private Context context;
    private PlaySoloView playSoloView;

    public PlaySoloPresenter(Context context, PlaySoloView playSoloView) {
        super(context, playSoloView);
        this.context = context;
        this.playSoloView = playSoloView;
    }

    public void callPlayQuizApi(String quizId) {
        HashMap<String, String> headerMap = new HashMap<>();
        headerMap.put("Authorization", "Bearer " + AppPreferences.getInstance(context).getPreferencesString(AppConstants.ACCESS_TOKEN));
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("quiz_id", quizId);
        jsonObject.addProperty("channel_id", "");
        NetworkManager.getApi().callPlayQuizEvent(headerMap, jsonObject).enqueue(this);
    }

    public void insertQuizDataApi(String quizId, String questionId, String choiceId, String answer, String score, String attemptedTime) {
        HashMap<String, String> headerMap = new HashMap<>();
        headerMap.put("Authorization", "Bearer " + AppPreferences.getInstance(context).getPreferencesString(AppConstants.ACCESS_TOKEN));
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("quiz_id", quizId);
        jsonObject.addProperty("channel_id", "");
        jsonObject.addProperty("question_id", questionId);
        jsonObject.addProperty("choice_id", choiceId);
        jsonObject.addProperty("answer", answer);
        jsonObject.addProperty("score", score);
        jsonObject.addProperty("attempted_time", attemptedTime);
        jsonObject.addProperty("type", "solo");
        NetworkManager.getApi().insertPlaySoloQuizData(headerMap, jsonObject).enqueue(this);
    }

    @Override
    public boolean handleFailure(Request request, Exception ex, String message) {
        if (playSoloView != null) {
            playSoloView.hideLoader();
            if (Helper.isContainValue(message)) {
                playSoloView.showMessage(message);
            }
        }
        return super.handleFailure(request, ex, message);
    }

    @Override
    public boolean handleError(Request request, Error error) {
        if (playSoloView != null) {
            playSoloView.hideLoader();
            playSoloView.showMessage(error.getMessage());
        }
        return super.handleError(request, error);
    }

    @Override
    public boolean handleResponse(Request request, BaseResponse response) {
        if (playSoloView != null) {
            playSoloView.hideLoader();
            if (response instanceof QuizStartResponse) {
                QuizStartResponse quizStartResponse = (QuizStartResponse) response;
                playSoloView.setPlaySoloResponse(quizStartResponse);
            } else if (response instanceof InsertQuizQuestionResponse) {
                InsertQuizQuestionResponse insertQuizQuestionResponse = (InsertQuizQuestionResponse) response;
                playSoloView.insertQuizDataResponse(insertQuizQuestionResponse);
            }
        }
        return true;
    }

    public void onDestroyedView() {
        playSoloView = null;
    }
}