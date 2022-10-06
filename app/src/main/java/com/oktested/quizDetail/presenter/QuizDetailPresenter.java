package com.oktested.quizDetail.presenter;

import android.content.Context;
import android.util.Log;

import com.google.gson.JsonObject;
import com.oktested.core.model.BaseResponse;
import com.oktested.core.network.NetworkHandler;
import com.oktested.core.network.NetworkManager;
import com.oktested.home.model.AcceptFriendResponse;
import com.oktested.home.model.ContactFileLinkResponse;
import com.oktested.home.model.ContactFileUploadResponse;
import com.oktested.home.model.FriendListResponse;
import com.oktested.home.model.GenerateInviteLinkResponse;
import com.oktested.home.model.SendFriendRequestResponse;
import com.oktested.home.model.UnFriendResponse;
import com.oktested.home.model.UserContactListResponse;
import com.oktested.quizDetail.model.InviteFriendToRoomResponse;
import com.oktested.quizDetail.model.QuizDetailResponse;
import com.oktested.quizDetail.ui.QuizDetailView;
import com.oktested.roomDatabase.model.ContactModel;
import com.oktested.utils.AppConstants;
import com.oktested.utils.AppPreferences;
import com.oktested.utils.Helper;
import com.oktested.videoCall.model.CameraStatusResponse;

import java.io.File;
import java.util.HashMap;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;

public class QuizDetailPresenter extends NetworkHandler {

    private Context context;
    private QuizDetailView quizDetailView;
    private  int position;
    private  ContactModel contactModel;
    private  String mobileNumber;
    String id;

    public QuizDetailPresenter(Context context, QuizDetailView quizDetailView) {
        super(context, quizDetailView);
        this.context = context;
        this.quizDetailView = quizDetailView;
    }

    public void callQuizDetailApi(String post_id) {
        NetworkManager.getOkTestedQuizApi().getQuizDetail(post_id).enqueue(this);
    }

    public void inviteFriendToRoomApi(String quizId, String friendId) {
        HashMap<String, String> headerMap = new HashMap<>();
        headerMap.put("Authorization", "Bearer " + AppPreferences.getInstance(context).getPreferencesString(AppConstants.ACCESS_TOKEN));
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("quiz_id", quizId);
        jsonObject.addProperty("friend_id", friendId);
        jsonObject.addProperty("channel_id", "");
        jsonObject.addProperty("firebase_token", AppPreferences.getInstance(context).getPreferencesString(AppConstants.FIREBASE_TOKEN));
        NetworkManager.getApi().inviteFriendToRoom(headerMap, jsonObject).enqueue(this);
    }

    public void callFriendListApi(String type,int page) {
        HashMap<String, String> headerMap = new HashMap<>();
        headerMap.put("Authorization", "Bearer " + AppPreferences.getInstance(context).getPreferencesString(AppConstants.ACCESS_TOKEN));
        NetworkManager.getApi().getFriendList(headerMap,type,page).enqueue(this);
    }

    public void callContactFetchingApi(String offset,String type) {
        HashMap<String, String> headerMap = new HashMap<>();
        headerMap.put("Authorization", "Bearer " + AppPreferences.getInstance(context).getPreferencesString(AppConstants.ACCESS_TOKEN));
        NetworkManager.getApi().getContactList(headerMap,type, offset, "100").enqueue(this);
    }

    public void callContactUploadApi(File file) {
        HashMap<String, String> headerMap = new HashMap<>();
        headerMap.put("Authorization", "Bearer " + AppPreferences.getInstance(context).getPreferencesString(AppConstants.ACCESS_TOKEN));
        String firebase =AppPreferences.getInstance(context).getPreferencesString(AppConstants.FIREBASE_TOKEN);
        Log.e("token",firebase);
        RequestBody reqFile = RequestBody.create(MediaType.parse("*/*"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), reqFile);
        RequestBody firebase1 = RequestBody.create(MediaType.parse("text/plain"), firebase);
        NetworkManager.getContactApi().uploadContacts(headerMap, body,firebase1).enqueue(this);
    }

    public void callSendFriendApi(String inviteUserId, String contactNumber, String connectType, int position, ContactModel contactModel) {
        this.position = position;
        this.contactModel = contactModel;
        HashMap<String, String> headerMap = new HashMap<>();
        headerMap.put("Authorization", "Bearer " + AppPreferences.getInstance(context).getPreferencesString(AppConstants.ACCESS_TOKEN));
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("invite_user_id", inviteUserId);
        jsonObject.addProperty("contact", contactNumber);
        jsonObject.addProperty("connect_type", connectType);
        NetworkManager.getApi().sendFriendRequest(headerMap, jsonObject).enqueue(this);
    }
    public void callAcceptFriendRequestApi(ContactModel contactModel,String connectType,int position) {
        this.position = position;
        this.contactModel = contactModel;
        HashMap<String, String> headerMap = new HashMap<>();
        headerMap.put("Authorization", "Bearer " + AppPreferences.getInstance(context).getPreferencesString(AppConstants.ACCESS_TOKEN));
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("sender_id",contactModel.id);
        jsonObject.addProperty("contact",contactModel.contact_no);
        jsonObject.addProperty("connect_type",connectType);
        NetworkManager.getApi().acceptFriendRequest(headerMap,jsonObject).enqueue(this);
    }

    public void callUnfriendApi(String friendId,int position) {
        this.position = position;
        this.id = friendId;

        HashMap<String, String> headerMap = new HashMap<>();
        headerMap.put("Authorization", "Bearer " + AppPreferences.getInstance(context).getPreferencesString(AppConstants.ACCESS_TOKEN));
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("friend_id", friendId);
        NetworkManager.getApi().unFriendApi(headerMap, jsonObject).enqueue(this);
    }

    public void callGenerateInviteLinkApi(String contactNumber, String connectType) {
        HashMap<String, String> headerMap = new HashMap<>();
        this.mobileNumber=contactNumber;
        headerMap.put("Authorization", "Bearer " + AppPreferences.getInstance(context).getPreferencesString(AppConstants.ACCESS_TOKEN));
        NetworkManager.getApi().getInviteLink(headerMap, contactNumber, connectType).enqueue(this);
    }

    public void callContactSearchApi(String search, String searchPage) {
        HashMap<String, String> headerMap = new HashMap<>();
        headerMap.put("Authorization", "Bearer " + AppPreferences.getInstance(context).getPreferencesString(AppConstants.ACCESS_TOKEN));
        NetworkManager.getApi().getSearchContactList(headerMap, search, searchPage, "100").enqueue(this);
    }

    public void callContactFileLink() {
        HashMap<String, String> headerMap = new HashMap<>();
        headerMap.put("Authorization", "Bearer " + AppPreferences.getInstance(context).getPreferencesString(AppConstants.ACCESS_TOKEN));
        NetworkManager.getApi().getContactFileLink(headerMap).enqueue(this);
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
    public boolean handleError(Request request, Error error) {
        if (quizDetailView != null) {
            quizDetailView.hideLoader();
            quizDetailView.showMessage(error.getMessage());
        }
        return super.handleError(request, error);
    }

    @Override
    public boolean handleFailure(Request request, Exception ex, String message) {
        if (quizDetailView != null) {
            quizDetailView.hideLoader();
            if (Helper.isContainValue(message)) {
                quizDetailView.showMessage(message);
            }
        }
        return super.handleFailure(request, ex, message);
    }

    @Override
    public boolean handleResponse(Request request, BaseResponse response) {
        if (quizDetailView != null) {
            quizDetailView.hideLoader();
            if (response instanceof QuizDetailResponse) {
                QuizDetailResponse quizDetailModel = (QuizDetailResponse) response;
                quizDetailView.setQuizDetailResponse(quizDetailModel);
            } else if (response instanceof InviteFriendToRoomResponse) {
                InviteFriendToRoomResponse inviteFriendToRoomResponse = (InviteFriendToRoomResponse) response;
                quizDetailView.setInviteFriendToRoomResponse(inviteFriendToRoomResponse);
            } else if (response instanceof FriendListResponse) {
                FriendListResponse friendListResponse = (FriendListResponse) response;
                quizDetailView.setFriendListResponse(friendListResponse);
            } else if (response instanceof UserContactListResponse) {
                UserContactListResponse userContactListResponse = (UserContactListResponse) response;
                quizDetailView.setContactListResponse(userContactListResponse);
            } else if (response instanceof ContactFileUploadResponse) {
                ContactFileUploadResponse contactFileUploadResponse = (ContactFileUploadResponse) response;
                quizDetailView.setUploadContactResponse(contactFileUploadResponse);
            } else if (response instanceof SendFriendRequestResponse) {
                SendFriendRequestResponse sendFriendRequestResponse = (SendFriendRequestResponse) response;
                quizDetailView.setSendRequestResponse(sendFriendRequestResponse,position,contactModel);
            } else if (response instanceof GenerateInviteLinkResponse) {
                GenerateInviteLinkResponse generateInviteLinkResponse = (GenerateInviteLinkResponse) response;
                quizDetailView.setInviteLinkResponse(generateInviteLinkResponse,mobileNumber);
            }else if (response instanceof ContactFileLinkResponse) {
                ContactFileLinkResponse contactFileLinkResponse = (ContactFileLinkResponse) response;
                quizDetailView.setContactLinkResponse(contactFileLinkResponse);
            }else if (response instanceof UnFriendResponse) {
                UnFriendResponse unFriendResponse = (UnFriendResponse) response;
                quizDetailView.setUnFriendResponse(unFriendResponse,position,id);
            } else if (response instanceof AcceptFriendResponse) {
                AcceptFriendResponse acceptFriendResponse = (AcceptFriendResponse) response;
                quizDetailView.setAcceptFriendResponse(acceptFriendResponse, position, contactModel);
            } else if (response instanceof CameraStatusResponse) {
                CameraStatusResponse cameraStatusResponse = (CameraStatusResponse) response;
                quizDetailView.setCameraStatusResponse(cameraStatusResponse);
            }
        }
        return true;
    }

    public void onDestroyedView() {
        quizDetailView = null;
    }
}