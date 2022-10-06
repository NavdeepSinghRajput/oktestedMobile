package com.oktested.quizResult.presenter;

import android.content.Context;

import com.oktested.core.model.BaseResponse;
import com.oktested.core.network.NetworkHandler;
import com.oktested.core.network.NetworkManager;
import com.oktested.quizResult.model.GroupQuizResultResponse;
import com.oktested.quizResult.ui.GroupQuizResultView;
import com.oktested.utils.AppConstants;
import com.oktested.utils.AppPreferences;
import com.oktested.utils.Helper;

import java.util.HashMap;

import okhttp3.Request;

public class GroupQuizResultPresenter extends NetworkHandler {

    private Context context;
    private GroupQuizResultView groupQuizResultView;

    public GroupQuizResultPresenter(Context context, GroupQuizResultView groupQuizResultView) {
        super(context, groupQuizResultView);
        this.context = context;
        this.groupQuizResultView = groupQuizResultView;
    }

    public void callGroupQuizResultApi(String quiz_id, String channelId) {
        HashMap<String, String> headerMap = new HashMap<>();
        headerMap.put("Authorization", "Bearer " + AppPreferences.getInstance(context).getPreferencesString(AppConstants.ACCESS_TOKEN));
        NetworkManager.getApi().getGroupQuizResult(headerMap, quiz_id, channelId).enqueue(this);
    }

    @Override
    public boolean handleFailure(Request request, Exception ex, String message) {
        if (groupQuizResultView != null) {
            groupQuizResultView.hideLoader();
            if (Helper.isContainValue(message)) {
                groupQuizResultView.showMessage(message);
            }
        }
        return super.handleFailure(request, ex, message);
    }

    @Override
    public boolean handleError(Request request, Error error) {
        if (groupQuizResultView != null) {
            groupQuizResultView.hideLoader();
            groupQuizResultView.showMessage(error.getMessage());
        }
        return super.handleError(request, error);
    }

    @Override
    public boolean handleResponse(Request request, BaseResponse response) {
        if (groupQuizResultView != null) {
            groupQuizResultView.hideLoader();
            if (response instanceof GroupQuizResultResponse) {
                GroupQuizResultResponse groupQuizResultResponse = (GroupQuizResultResponse) response;
                groupQuizResultView.setGroupQuizResultResponse(groupQuizResultResponse);
            }
        }
        return true;
    }

    public void onDestroyedView() {
        groupQuizResultView = null;
    }
}