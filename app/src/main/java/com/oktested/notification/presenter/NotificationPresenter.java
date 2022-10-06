package com.oktested.notification.presenter;

import android.content.Context;

import com.google.gson.JsonObject;
import com.oktested.core.model.BaseResponse;
import com.oktested.core.network.NetworkHandler;
import com.oktested.core.network.NetworkManager;
import com.oktested.home.model.AcceptFriendResponse;
import com.oktested.home.model.RejectFriendResponse;
import com.oktested.notification.model.NotificationsResponse;
import com.oktested.notification.ui.NotificationView;
import com.oktested.utils.AppConstants;
import com.oktested.utils.AppPreferences;
import com.oktested.utils.Helper;

import java.util.HashMap;

import okhttp3.Request;

public class NotificationPresenter extends NetworkHandler {

    private Context context;
    private NotificationView notificationView;
    private int position;

    public NotificationPresenter(Context context, NotificationView notificationView) {
        super(context, notificationView);
        this.context = context;
        this.notificationView = notificationView;
    }

    public void callQuizDetailApi(String post_id) {
        NetworkManager.getOkTestedQuizApi().getQuizDetail(post_id).enqueue(this);
    }

    @Override
    public boolean handleError(Request request, Error error) {
        if (notificationView != null) {
            notificationView.hideLoader();
            notificationView.showMessage(error.getMessage());
        }
        return super.handleError(request, error);
    }

    @Override
    public boolean handleFailure(Request request, Exception ex, String message) {
        if (notificationView != null) {
            notificationView.hideLoader();
            if (Helper.isContainValue(message)) {
                notificationView.showMessage(message);
            }
        }
        return super.handleFailure(request, ex, message);
    }

    @Override
    public boolean handleResponse(Request request, BaseResponse response) {
        if (notificationView != null) {
            notificationView.hideLoader();
            if (response instanceof NotificationsResponse) {
                NotificationsResponse notificationsResponse = (NotificationsResponse) response;
                notificationView.setNotificationResponse(notificationsResponse);
            } else if (response instanceof AcceptFriendResponse) {
                AcceptFriendResponse notifcationModelItem = (AcceptFriendResponse) response;
                notificationView.setAcceptFriendResponse(notifcationModelItem, position);
            } else if (response instanceof RejectFriendResponse) {
                RejectFriendResponse rejectFriendResponse = (RejectFriendResponse) response;
                notificationView.setRejectFriendResponse(rejectFriendResponse, position);
            }
        }
        return true;
    }

    public void callNotificationApi(int page) {
        HashMap<String, String> headerMap = new HashMap<>();
        headerMap.put("Authorization", "Bearer " + AppPreferences.getInstance(context).getPreferencesString(AppConstants.ACCESS_TOKEN));
        NetworkManager.getApi().getUserNotification(headerMap, page, 15).enqueue(this);
    }

    public void callAcceptFriendRequestApi(String sender_id, String contactNumber, String connectType, int position) {
        this.position = position;
        HashMap<String, String> headerMap = new HashMap<>();
        headerMap.put("Authorization", "Bearer " + AppPreferences.getInstance(context).getPreferencesString(AppConstants.ACCESS_TOKEN));
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("sender_id", sender_id);
        jsonObject.addProperty("contact", contactNumber);
        jsonObject.addProperty("connect_type", connectType);
        NetworkManager.getApi().acceptFriendRequest(headerMap, jsonObject).enqueue(this);
    }

    public void callRejectFriendRequestApi(String sender_id, String contactNumber, String connectType, int position) {
        this.position = position;
        HashMap<String, String> headerMap = new HashMap<>();
        headerMap.put("Authorization", "Bearer " + AppPreferences.getInstance(context).getPreferencesString(AppConstants.ACCESS_TOKEN));
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("sender_id", sender_id);
        jsonObject.addProperty("contact", contactNumber);
        jsonObject.addProperty("connect_type", connectType);
        NetworkManager.getApi().rejectFriendRequest(headerMap, jsonObject).enqueue(this);
    }

    public void onDestroyedView() {
        notificationView = null;
    }
}