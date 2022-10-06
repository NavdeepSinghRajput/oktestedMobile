package com.oktested.allQuiz.presenter;

import android.content.Context;

import com.oktested.allQuiz.ui.AllQuizView;
import com.oktested.core.model.BaseResponse;
import com.oktested.core.network.NetworkHandler;
import com.oktested.core.network.NetworkManager;
import com.oktested.home.model.CategoryDetailQuizResponse;
import com.oktested.home.model.CategoryQuizResponse;
import com.oktested.home.model.LatestQuizResponse;
import com.oktested.home.model.TrendingQuizResponse;
import com.oktested.quizProfile.model.RecentPlayedQuizResponse;
import com.oktested.utils.AppConstants;
import com.oktested.utils.AppPreferences;
import com.oktested.utils.Helper;

import java.util.HashMap;

import okhttp3.Request;

public class AllQuizPresenter extends NetworkHandler {

    private Context context;
    private AllQuizView allQuizView;

    public AllQuizPresenter(Context context, AllQuizView allVideoView) {
        super(context, allVideoView);
        this.context = context;
        this.allQuizView = allVideoView;
    }

    public void callRecentPlayedQuizApi(String offset) {
        HashMap<String, String> headerMap = new HashMap<>();
        headerMap.put("Authorization", "Bearer " + AppPreferences.getInstance(context).getPreferencesString(AppConstants.ACCESS_TOKEN));
        NetworkManager.getApi().getRecentPlayedQuiz(headerMap,offset).enqueue(this);
    }
    public void callTrendingQuizApi(String section_type,String offset) {
        NetworkManager.getOkTestedQuizApi().getTrendingQuiz(section_type,offset).enqueue(this);
    }

    public void callLatestQuizApi(String offsets) {
        NetworkManager.getOkTestedQuizApi().getLatestQuiz(offsets).enqueue(this);
    }

    public void callCategoryQuizApi(String offsets) {
        NetworkManager.getOkTestedQuizApi().getQuizCategory(offsets).enqueue(this);
    }

    public void callCategoryDetailQuizApi(String category_slug,String offset) {
        NetworkManager.getOkTestedQuizApi().getQuizCategoryDetail(category_slug,offset).enqueue(this);
    }

    @Override
    public boolean handleError(Request request, Error error) {
        if (allQuizView != null) {
            allQuizView.showMessage(error.getMessage());
        }
        return super.handleError(request, error);
    }

    @Override
    public boolean handleFailure(Request request, Exception ex, String message) {
        if (allQuizView != null) {
            if (Helper.isContainValue(message)) {
                allQuizView.showMessage(message);
            }
        }
        return super.handleFailure(request, ex, message);
    }

    @Override
    public boolean handleResponse(Request request, BaseResponse response) {
        if (allQuizView != null) {
            if (response instanceof LatestQuizResponse) {
                LatestQuizResponse latestQuizResponse = (LatestQuizResponse) response;
                allQuizView.setLatestQuizResponse(latestQuizResponse);
            } else if (response instanceof TrendingQuizResponse) {
                TrendingQuizResponse trendingQuizResponse = (TrendingQuizResponse) response;
                allQuizView.setTrendingQuizResponse(trendingQuizResponse);
            } else if (response instanceof CategoryQuizResponse) {
                CategoryQuizResponse categoryQuizResponse = (CategoryQuizResponse) response;
                allQuizView.setCategoryQuizResponse(categoryQuizResponse);
            } else if (response instanceof CategoryDetailQuizResponse) {
                CategoryDetailQuizResponse categoryDetailQuizResponse = (CategoryDetailQuizResponse) response;
                allQuizView.setCategoryDetailQuizResponse(categoryDetailQuizResponse);
            } else if (response instanceof RecentPlayedQuizResponse) {
                RecentPlayedQuizResponse recentPlayedQuizResponse = (RecentPlayedQuizResponse) response;
                allQuizView.setRecentPlayedQuizResponse(recentPlayedQuizResponse);
            }
        }
        return true;
    }

    public void onDestroyedView() {
        allQuizView = null;
    }
}