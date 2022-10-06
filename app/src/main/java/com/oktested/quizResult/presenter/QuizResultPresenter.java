package com.oktested.quizResult.presenter;

import android.content.Context;

import com.oktested.core.model.BaseResponse;
import com.oktested.core.network.NetworkHandler;
import com.oktested.core.network.NetworkManager;
import com.oktested.quizResult.model.QuizResultResponse;
import com.oktested.quizResult.ui.QuizResultView;
import com.oktested.utils.AppConstants;
import com.oktested.utils.AppPreferences;
import com.oktested.utils.Helper;
import com.oktested.videoCall.model.RecommendedQuizResponse;

import java.util.HashMap;

import okhttp3.Request;

public class QuizResultPresenter extends NetworkHandler {

    private Context context;
    private QuizResultView quizResultView;

    public QuizResultPresenter(Context context, QuizResultView quizResultView) {
        super(context, quizResultView);
        this.context = context;
        this.quizResultView = quizResultView;
    }

    public void callRecommendedQuizApi(String quiz_id, String offset) {
        NetworkManager.getOkTestedQuizApi().getRecommendedQuiz(quiz_id, offset).enqueue(this);
    }

    public void callQuizResultApi(String quiz_id) {
        HashMap<String, String> headerMap = new HashMap<>();
        headerMap.put("Authorization", "Bearer " + AppPreferences.getInstance(context).getPreferencesString(AppConstants.ACCESS_TOKEN));
        NetworkManager.getApi().getSoloQuizResult(headerMap, quiz_id).enqueue(this);
    }

    @Override
    public boolean handleError(Request request, Error error) {
        if (quizResultView != null) {
            quizResultView.hideLoader();
            quizResultView.showMessage(error.getMessage());
        }
        return super.handleError(request, error);
    }

    @Override
    public boolean handleFailure(Request request, Exception ex, String message) {
        if (quizResultView != null) {
            quizResultView.hideLoader();
            if (Helper.isContainValue(message)) {
                quizResultView.showMessage(message);
            }
        }
        return super.handleFailure(request, ex, message);
    }

    @Override
    public boolean handleResponse(Request request, BaseResponse response) {
        if (quizResultView != null) {
            quizResultView.hideLoader();
            if (response instanceof QuizResultResponse) {
                QuizResultResponse quizResultResponse = (QuizResultResponse) response;
                quizResultView.setQuizResultResponse(quizResultResponse);
            } else if (response instanceof RecommendedQuizResponse) {
                RecommendedQuizResponse recommendedQuizResponse = (RecommendedQuizResponse) response;
                quizResultView.setRecommendedQuizResponse(recommendedQuizResponse);
            }
        }
        return true;
    }

    public void onDestroyedView() {
        quizResultView = null;
    }
}