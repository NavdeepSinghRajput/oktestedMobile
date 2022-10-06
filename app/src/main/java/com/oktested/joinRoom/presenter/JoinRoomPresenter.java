package com.oktested.joinRoom.presenter;

import android.content.Context;

import com.google.gson.JsonObject;
import com.oktested.core.model.BaseResponse;
import com.oktested.core.network.NetworkHandler;
import com.oktested.core.network.NetworkManager;
import com.oktested.joinRoom.model.JoinRoomResponse;
import com.oktested.joinRoom.model.RejectInvitationResponse;
import com.oktested.joinRoom.ui.JoinRoomView;
import com.oktested.utils.AppConstants;
import com.oktested.utils.AppPreferences;
import com.oktested.utils.Helper;
import com.oktested.videoCall.model.CameraStatusResponse;

import java.util.HashMap;

import okhttp3.Request;

public class JoinRoomPresenter extends NetworkHandler {

    private Context context;
    private JoinRoomView joinRoomView;

    public JoinRoomPresenter(Context context, JoinRoomView joinRoomView) {
        super(context, joinRoomView);
        this.context = context;
        this.joinRoomView = joinRoomView;
    }

    public void joinRoomApi(String channelId) {
        HashMap<String, String> headerMap = new HashMap<>();
        headerMap.put("Authorization", "Bearer " + AppPreferences.getInstance(context).getPreferencesString(AppConstants.ACCESS_TOKEN));
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("action", "join");
        jsonObject.addProperty("channel_id", channelId);
        jsonObject.addProperty("firebase_token", AppPreferences.getInstance(context).getPreferencesString(AppConstants.FIREBASE_TOKEN));
        NetworkManager.getApi().joinRoom(headerMap, jsonObject).enqueue(this);
    }

    public void rejectInvitationApi(String channelId) {
        HashMap<String, String> headerMap = new HashMap<>();
        headerMap.put("Authorization", "Bearer " + AppPreferences.getInstance(context).getPreferencesString(AppConstants.ACCESS_TOKEN));
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("channel_id", channelId);
        NetworkManager.getApi().rejectInvitation(headerMap, jsonObject).enqueue(this);
    }

    public void changeCameraStatusApi(String channelId, String action) {
        HashMap<String, String> headerMap = new HashMap<>();
        headerMap.put("Authorization", "Bearer " + AppPreferences.getInstance(context).getPreferencesString(AppConstants.ACCESS_TOKEN));
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("user_id", AppPreferences.getInstance(context).getPreferencesString(AppConstants.UID));
        jsonObject.addProperty("channel_id", channelId);
        NetworkManager.getApi().changeCameraStatus(headerMap, action, jsonObject).enqueue(this);
    }

    @Override
    public boolean handleFailure(Request request, Exception ex, String message) {
        if (joinRoomView != null) {
            joinRoomView.hideLoader();
            if (Helper.isContainValue(message)) {
                joinRoomView.showMessage(message);
            }
        }
        return super.handleFailure(request, ex, message);
    }

    @Override
    public boolean handleError(Request request, Error error) {
        if (joinRoomView != null) {
            joinRoomView.hideLoader();
            joinRoomView.showMessage(error.getMessage());
        }
        return super.handleError(request, error);
    }

    @Override
    public boolean handleResponse(Request request, BaseResponse response) {
        if (joinRoomView != null) {
            joinRoomView.hideLoader();
            if (response instanceof JoinRoomResponse) {
                JoinRoomResponse joinRoomResponse = (JoinRoomResponse) response;
                joinRoomView.setJoinRoomResponse(joinRoomResponse);
            } else if (response instanceof RejectInvitationResponse) {
                RejectInvitationResponse rejectInvitationResponse = (RejectInvitationResponse) response;
                joinRoomView.setRejectInvitationResponse(rejectInvitationResponse);
            } else if (response instanceof CameraStatusResponse) {
                CameraStatusResponse cameraStatusResponse = (CameraStatusResponse) response;
                joinRoomView.setCameraStatusResponse(cameraStatusResponse);
            }
        }
        return true;
    }

    public void onDestroyedView() {
        joinRoomView = null;
    }
}