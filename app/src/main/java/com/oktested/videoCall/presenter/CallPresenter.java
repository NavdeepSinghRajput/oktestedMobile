package com.oktested.videoCall.presenter;

import android.content.Context;

import com.google.gson.JsonObject;
import com.oktested.core.model.BaseResponse;
import com.oktested.core.network.NetworkHandler;
import com.oktested.core.network.NetworkManager;
import com.oktested.home.model.FriendListResponse;
import com.oktested.playSolo.model.InsertQuizQuestionResponse;
import com.oktested.playSolo.model.QuizStartResponse;
import com.oktested.quizDetail.model.InviteFriendToRoomResponse;
import com.oktested.quizDetail.model.QuizDetailResponse;
import com.oktested.quizResult.model.GroupQuizResultResponse;
import com.oktested.utils.AppConstants;
import com.oktested.utils.AppPreferences;
import com.oktested.utils.Helper;
import com.oktested.videoCall.model.AcceptSuggestedQuizResponse;
import com.oktested.videoCall.model.CameraStatusResponse;
import com.oktested.videoCall.model.ChannelDetailResponse;
import com.oktested.videoCall.model.LeaveRoomResponse;
import com.oktested.videoCall.model.QuestionWiseResultResponse;
import com.oktested.videoCall.model.RecommendedQuizResponse;
import com.oktested.videoCall.model.RemoveParticipantResponse;
import com.oktested.videoCall.ui.CallView;

import java.util.HashMap;

import okhttp3.Request;

public class CallPresenter extends NetworkHandler {

    private Context context;
    private CallView callView;

    public CallPresenter(Context context, CallView callView) {
        super(context, callView);
        this.context = context;
        this.callView = callView;
    }

    public void leaveRoomApi(String channelId) {
        HashMap<String, String> headerMap = new HashMap<>();
        headerMap.put("Authorization", "Bearer " + AppPreferences.getInstance(context).getPreferencesString(AppConstants.ACCESS_TOKEN));
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("action", "left");
        jsonObject.addProperty("channel_id", channelId);
        jsonObject.addProperty("firebase_token", AppPreferences.getInstance(context).getPreferencesString(AppConstants.FIREBASE_TOKEN));
        NetworkManager.getApi().leaveRoom(headerMap, jsonObject).enqueue(this);
    }

    public void callPlayQuizApi(String quizId, String channelName) {
        HashMap<String, String> headerMap = new HashMap<>();
        headerMap.put("Authorization", "Bearer " + AppPreferences.getInstance(context).getPreferencesString(AppConstants.ACCESS_TOKEN));
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("quiz_id", quizId);
        jsonObject.addProperty("channel_id", channelName);
        NetworkManager.getApi().callGroupPlayQuizEvent(headerMap, jsonObject).enqueue(this);
    }

    public void insertQuizDataApi(String quizId, String questionId, String choiceId, String answer, String score, String attemptedTime, String channelName) {
        HashMap<String, String> headerMap = new HashMap<>();
        headerMap.put("Authorization", "Bearer " + AppPreferences.getInstance(context).getPreferencesString(AppConstants.ACCESS_TOKEN));
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("quiz_id", quizId);
        jsonObject.addProperty("channel_id", channelName);
        jsonObject.addProperty("question_id", questionId);
        jsonObject.addProperty("choice_id", choiceId);
        jsonObject.addProperty("answer", answer);
        jsonObject.addProperty("score", score);
        jsonObject.addProperty("attempted_time", attemptedTime);
        jsonObject.addProperty("type", "group");
        NetworkManager.getApi().insertPlaySoloQuizData(headerMap, jsonObject).enqueue(this);
    }

    public void callQuestionWiseResult(String channelId, String quizId, String questionId) {
        HashMap<String, String> headerMap = new HashMap<>();
        headerMap.put("Authorization", "Bearer " + AppPreferences.getInstance(context).getPreferencesString(AppConstants.ACCESS_TOKEN));
        NetworkManager.getApi().getQuestionWiseResult(headerMap, channelId, quizId, questionId).enqueue(this);
    }

    public void callGroupQuizResultApi(String quiz_id, String channelId) {
        HashMap<String, String> headerMap = new HashMap<>();
        headerMap.put("Authorization", "Bearer " + AppPreferences.getInstance(context).getPreferencesString(AppConstants.ACCESS_TOKEN));
        NetworkManager.getApi().getGroupQuizResult(headerMap, quiz_id, channelId).enqueue(this);
    }

    public void callFriendListApi(String type, int page) {
        HashMap<String, String> headerMap = new HashMap<>();
        headerMap.put("Authorization", "Bearer " + AppPreferences.getInstance(context).getPreferencesString(AppConstants.ACCESS_TOKEN));
        NetworkManager.getApi().getFriendList(headerMap, type, page).enqueue(this);
    }

    public void inviteFriendToRoomApi(String quizId, String friendId, String channelName) {
        HashMap<String, String> headerMap = new HashMap<>();
        headerMap.put("Authorization", "Bearer " + AppPreferences.getInstance(context).getPreferencesString(AppConstants.ACCESS_TOKEN));
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("quiz_id", quizId);
        jsonObject.addProperty("friend_id", friendId);
        jsonObject.addProperty("channel_id", channelName);
        jsonObject.addProperty("firebase_token", AppPreferences.getInstance(context).getPreferencesString(AppConstants.FIREBASE_TOKEN));
        NetworkManager.getApi().inviteFriendToRoom(headerMap, jsonObject).enqueue(this);
    }

    public void callRecommendedQuizApi(String quiz_id, String offset) {
        NetworkManager.getOkTestedQuizApi().getRecommendedQuiz(quiz_id, offset).enqueue(this);
    }

    public void callQuizDetailApi(String post_id, String channelId) {
        NetworkManager.getOkTestedQuizApi().getQuizDetailForChannel(post_id, channelId).enqueue(this);
    }

    public void callAcceptSuggestedQuizApi(String quizId, String channelName, String participantId) {
        HashMap<String, String> headerMap = new HashMap<>();
        headerMap.put("Authorization", "Bearer " + AppPreferences.getInstance(context).getPreferencesString(AppConstants.ACCESS_TOKEN));
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("quiz_id", quizId);
        jsonObject.addProperty("channel_id", channelName);
        jsonObject.addProperty("participant_id", participantId);
        NetworkManager.getApi().acceptSuggestedQuiz(headerMap, jsonObject).enqueue(this);
    }

    public void getChannelDetailApi(String channelId) {
        HashMap<String, String> headerMap = new HashMap<>();
        headerMap.put("Authorization", "Bearer " + AppPreferences.getInstance(context).getPreferencesString(AppConstants.ACCESS_TOKEN));
        NetworkManager.getApi().getDetailChannelData(headerMap, channelId).enqueue(this);
    }

    public void callRemoveParticipantApi(String participantId, String channelName) {
        HashMap<String, String> headerMap = new HashMap<>();
        headerMap.put("Authorization", "Bearer " + AppPreferences.getInstance(context).getPreferencesString(AppConstants.ACCESS_TOKEN));
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("channel_id", channelName);
        jsonObject.addProperty("participant_id", participantId);
        NetworkManager.getApi().removeParticipant(headerMap, jsonObject).enqueue(this);
    }

    public void callReadyUnreadyApi(String action, String channelName) {
        HashMap<String, String> headerMap = new HashMap<>();
        headerMap.put("Authorization", "Bearer " + AppPreferences.getInstance(context).getPreferencesString(AppConstants.ACCESS_TOKEN));
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("channel_id", channelName);
        jsonObject.addProperty("action", action);
        NetworkManager.getApi().readyToPlay(headerMap, jsonObject).enqueue(this);
    }

    public void changeCameraStatusMultipleUserApi(String channelId, String action) {
        HashMap<String, String> headerMap = new HashMap<>();
        headerMap.put("Authorization", "Bearer " + AppPreferences.getInstance(context).getPreferencesString(AppConstants.ACCESS_TOKEN));
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("user_id", AppPreferences.getInstance(context).getPreferencesString(AppConstants.UID));
        jsonObject.addProperty("channel_id", channelId);
        jsonObject.addProperty("is_notify", "yes");
        NetworkManager.getApi().changeCameraStatus(headerMap, action, jsonObject).enqueue(this);
    }

    public void changeCameraStatusSingleUserApi(String channelId, String action) {
        HashMap<String, String> headerMap = new HashMap<>();
        headerMap.put("Authorization", "Bearer " + AppPreferences.getInstance(context).getPreferencesString(AppConstants.ACCESS_TOKEN));
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("user_id", AppPreferences.getInstance(context).getPreferencesString(AppConstants.UID));
        jsonObject.addProperty("channel_id", channelId);
        NetworkManager.getApi().changeCameraStatus(headerMap, action, jsonObject).enqueue(this);
    }

    @Override
    public boolean handleFailure(Request request, Exception ex, String message) {
        if (callView != null) {
            callView.hideLoader();
            if (Helper.isContainValue(message)) {
                callView.showMessage(message);
            }
        }
        return super.handleFailure(request, ex, message);
    }

    @Override
    public boolean handleError(Request request, Error error) {
        if (callView != null) {
            callView.hideLoader();
            callView.showMessage(error.getMessage());
        }
        return super.handleError(request, error);
    }

    @Override
    public boolean handleResponse(Request request, BaseResponse response) {
        if (callView != null) {
            callView.hideLoader();
            if (response instanceof QuizStartResponse) {
                QuizStartResponse quizStartResponse = (QuizStartResponse) response;
                callView.setPlayGroupResponse(quizStartResponse);
            } else if (response instanceof InsertQuizQuestionResponse) {
                InsertQuizQuestionResponse insertQuizQuestionResponse = (InsertQuizQuestionResponse) response;
                callView.insertQuizDataResponse(insertQuizQuestionResponse);
            } else if (response instanceof LeaveRoomResponse) {
                LeaveRoomResponse leaveRoomResponse = (LeaveRoomResponse) response;
                callView.setLeaveRoomResponse(leaveRoomResponse);
            } else if (response instanceof QuestionWiseResultResponse) {
                QuestionWiseResultResponse questionWiseResultResponse = (QuestionWiseResultResponse) response;
                callView.setQuestionWiseResultResponse(questionWiseResultResponse);
            } else if (response instanceof GroupQuizResultResponse) {
                GroupQuizResultResponse groupQuizResultResponse = (GroupQuizResultResponse) response;
                callView.setGroupQuizResultResponse(groupQuizResultResponse);
            } else if (response instanceof FriendListResponse) {
                FriendListResponse friendListResponse = (FriendListResponse) response;
                callView.setFriendListResponse(friendListResponse);
            } else if (response instanceof InviteFriendToRoomResponse) {
                InviteFriendToRoomResponse inviteFriendToRoomResponse = (InviteFriendToRoomResponse) response;
                callView.setInviteFriendToRoomResponse(inviteFriendToRoomResponse);
            } else if (response instanceof RecommendedQuizResponse) {
                RecommendedQuizResponse recommendedQuizResponse = (RecommendedQuizResponse) response;
                callView.setRecommendedQuizResponse(recommendedQuizResponse);
            } else if (response instanceof QuizDetailResponse) {
                QuizDetailResponse quizDetailModel = (QuizDetailResponse) response;
                callView.setQuizDetailResponse(quizDetailModel);
            } else if (response instanceof AcceptSuggestedQuizResponse) {
                AcceptSuggestedQuizResponse acceptSuggestedQuizResponse = (AcceptSuggestedQuizResponse) response;
                callView.setAcceptSuggestedQuizResponse(acceptSuggestedQuizResponse);
            } else if (response instanceof ChannelDetailResponse) {
                ChannelDetailResponse channelDetailResponse = (ChannelDetailResponse) response;
                callView.setChannelDetailResponse(channelDetailResponse);
            } else if (response instanceof RemoveParticipantResponse) {
                RemoveParticipantResponse removeParticipantResponse = (RemoveParticipantResponse) response;
                callView.setRemoveParticipantResponse(removeParticipantResponse);
            } else if (response instanceof CameraStatusResponse) {
                CameraStatusResponse cameraStatusResponse = (CameraStatusResponse) response;
                callView.setCameraStatusResponse(cameraStatusResponse);
            }
        }
        return true;
    }

    public void onDestroyedView() {
        callView = null;
    }
}