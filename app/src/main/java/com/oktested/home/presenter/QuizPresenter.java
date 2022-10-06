package com.oktested.home.presenter;

import android.content.Context;

import com.google.gson.JsonObject;
import com.oktested.core.model.BaseResponse;
import com.oktested.core.network.NetworkHandler;
import com.oktested.core.network.NetworkManager;
import com.oktested.home.model.AcceptFriendResponse;
import com.oktested.home.model.CategoryDetailQuizResponse;
import com.oktested.home.model.CategoryQuizResponse;
import com.oktested.home.model.ContactFileLinkResponse;
import com.oktested.home.model.ContactFileUploadResponse;
import com.oktested.home.model.FriendListResponse;
import com.oktested.home.model.GenerateInviteLinkResponse;
import com.oktested.home.model.LatestQuizResponse;
import com.oktested.home.model.QuizStructureResponse;
import com.oktested.home.model.SearchQuizResponse;
import com.oktested.home.model.SendFriendRequestResponse;
import com.oktested.home.model.TrendingQuizResponse;
import com.oktested.home.model.UnFriendResponse;
import com.oktested.home.model.UserContactListResponse;
import com.oktested.home.ui.QuizView;
import com.oktested.quizDetail.model.InviteFriendToRoomResponse;
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

public class QuizPresenter extends NetworkHandler {

    private Context context;
    private QuizView quizView;
    int position;
    int size;
    String id;
    ContactModel contactModel;
    String mobileNumber;

    public QuizPresenter(Context context, QuizView quizView) {
        super(context, quizView);
        this.context = context;
        this.quizView = quizView;
    }

    @Override
    public boolean handleError(Request request, Error error) {
        if (quizView != null) {
            quizView.hideLoader();
            quizView.showMessage(error.getMessage());
        }
        return super.handleError(request, error);
    }

    @Override
    public boolean handleFailure(Request request, Exception ex, String message) {
        if (quizView != null) {
            quizView.hideLoader();
            if (Helper.isContainValue(message)) {
                quizView.showMessage(message);
            }
        }
        return super.handleFailure(request, ex, message);
    }

    @Override
    public boolean handleResponse(Request request, BaseResponse response) {
        if (quizView != null) {
            if (response instanceof QuizStructureResponse) {
                QuizStructureResponse quizStructureResponse = (QuizStructureResponse) response;
                quizView.setQuizStructureResponse(quizStructureResponse);
            } else if (response instanceof TrendingQuizResponse) {
                TrendingQuizResponse tredingQuizResponse = (TrendingQuizResponse) response;
                quizView.setTrendingQuizResponse(tredingQuizResponse);
            } else if (response instanceof LatestQuizResponse) {
                LatestQuizResponse latestQuizResponse = (LatestQuizResponse) response;
                quizView.setLatestQuizResponse(latestQuizResponse);
            } else if (response instanceof CategoryQuizResponse) {
                CategoryQuizResponse categoryQuizResponse = (CategoryQuizResponse) response;
                quizView.setCategoryQuizResponse(categoryQuizResponse);
            } else if (response instanceof CategoryDetailQuizResponse) {
                CategoryDetailQuizResponse categoryQuizResponse = (CategoryDetailQuizResponse) response;
                quizView.setCategoryDetailQuizResponse(categoryQuizResponse);
            } else if (response instanceof SearchQuizResponse) {
                SearchQuizResponse searchQuizResponse = (SearchQuizResponse) response;
                quizView.setSearchQuizResponse(searchQuizResponse);
            } else if (response instanceof ContactFileUploadResponse) {
                ContactFileUploadResponse contactFileUploadResponse = (ContactFileUploadResponse) response;
                quizView.setUploadContactResponse(contactFileUploadResponse);
            } else if (response instanceof UserContactListResponse) {
                UserContactListResponse userContactListResponse = (UserContactListResponse) response;
                quizView.setContactListResponse(userContactListResponse);
            } else if (response instanceof SendFriendRequestResponse) {
                SendFriendRequestResponse sendFriendRequestResponse = (SendFriendRequestResponse) response;
                quizView.setSendRequestResponse(sendFriendRequestResponse, position, contactModel);
            } else if (response instanceof GenerateInviteLinkResponse) {
                GenerateInviteLinkResponse generateInviteLinkResponse = (GenerateInviteLinkResponse) response;
                quizView.setInviteLinkResponse(generateInviteLinkResponse, mobileNumber);
            } else if (response instanceof FriendListResponse) {
                FriendListResponse friendListResponse = (FriendListResponse) response;
                quizView.setFriendListResponse(friendListResponse);
            } else if (response instanceof InviteFriendToRoomResponse) {
                InviteFriendToRoomResponse inviteFriendToRoomResponse = (InviteFriendToRoomResponse) response;
                quizView.setInviteFriendToRoomResponse(inviteFriendToRoomResponse);
            }/* else if (response instanceof SearchUserContactListResponse) {
                SearchUserContactListResponse searchUserContactListResponse = (SearchUserContactListResponse) response;
                quizView.setSeacrhContactListResponse(searchUserContactListResponse);
            }*/ else if (response instanceof ContactFileLinkResponse) {
                ContactFileLinkResponse contactFileLinkResponse = (ContactFileLinkResponse) response;
                quizView.setContactLinkResponse(contactFileLinkResponse);
            } else if (response instanceof UnFriendResponse) {
                UnFriendResponse unFriendResponse = (UnFriendResponse) response;
                quizView.setUnFriendResponse(unFriendResponse, size, id);
            } else if (response instanceof AcceptFriendResponse) {
                AcceptFriendResponse acceptFriendResponse = (AcceptFriendResponse) response;
                quizView.setAcceptFriendResponse(acceptFriendResponse, position, contactModel);
            } else if (response instanceof CameraStatusResponse) {
                CameraStatusResponse cameraStatusResponse = (CameraStatusResponse) response;
                quizView.setCameraStatusResponse(cameraStatusResponse);
            }
        }
        return true;
    }

    public void callAcceptFriendRequestApi(ContactModel contactModel, String connectType, int position) {
        this.position = position;
        this.contactModel = contactModel;
        HashMap<String, String> headerMap = new HashMap<>();
        headerMap.put("Authorization", "Bearer " + AppPreferences.getInstance(context).getPreferencesString(AppConstants.ACCESS_TOKEN));
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("sender_id", contactModel.id);
        jsonObject.addProperty("contact", contactModel.contact_no);
        jsonObject.addProperty("connect_type", connectType);
        NetworkManager.getApi().acceptFriendRequest(headerMap, jsonObject).enqueue(this);
    }

    public void callQuizStructure() {
        NetworkManager.getOkTestedQuizApi().getQuizHomeStructure().enqueue(this);
    }

    public void callTrendingQuizApi(String section_type, String offsets) {
        NetworkManager.getOkTestedQuizApi().getTrendingQuiz(section_type, offsets).enqueue(this);
    }

    public void callLatestQuizApi(String offset) {
        NetworkManager.getOkTestedQuizApi().getLatestQuiz(offset).enqueue(this);
    }

    public void callCategoryQuizApi(String offset) {
        NetworkManager.getOkTestedQuizApi().getQuizCategory(offset).enqueue(this);
    }

    public void callCategoryDetailQuizApi(String category_slug, String offset) {
        NetworkManager.getOkTestedQuizApi().getQuizCategoryDetail(category_slug, offset).enqueue(this);
    }

    public void callQuizSearchApi(String searchString, String page) {
        NetworkManager.getOkTestedQuizApi().getSearchedQuiz(searchString, page).enqueue(this);
    }

    public void callContactUploadApi(File file) {
        HashMap<String, String> headerMap = new HashMap<>();
        headerMap.put("Authorization", "Bearer " + AppPreferences.getInstance(context).getPreferencesString(AppConstants.ACCESS_TOKEN));
        String firebase = AppPreferences.getInstance(context).getPreferencesString(AppConstants.FIREBASE_TOKEN);
        RequestBody reqFile = RequestBody.create(MediaType.parse("*/*"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), reqFile);
        RequestBody firebase1 = RequestBody.create(MediaType.parse("text/plain"), firebase);
        NetworkManager.getContactApi().uploadContacts(headerMap, body, firebase1).enqueue(this);
    }

   /* public void callContactFetchingApi(String offset,String type) {
        HashMap<String, String> headerMap = new HashMap<>();
        headerMap.put("Authorization", "Bearer " + AppPreferences.getInstance(context).getPreferencesString(AppConstants.ACCESS_TOKEN));
        NetworkManager.getApi().getContactList(headerMap,type, offset, "100").enqueue(this);
    }*/

    public void callContactSearchApi(String search, String searchPage) {
        HashMap<String, String> headerMap = new HashMap<>();
        headerMap.put("Authorization", "Bearer " + AppPreferences.getInstance(context).getPreferencesString(AppConstants.ACCESS_TOKEN));
        NetworkManager.getApi().getSearchContactList(headerMap, search, searchPage, "100").enqueue(this);
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

    public void callGenerateInviteLinkApi(String contactNumber, String connectType) {
        this.mobileNumber = contactNumber;
        HashMap<String, String> headerMap = new HashMap<>();
        headerMap.put("Authorization", "Bearer " + AppPreferences.getInstance(context).getPreferencesString(AppConstants.ACCESS_TOKEN));
        NetworkManager.getApi().getInviteLink(headerMap, contactNumber, connectType).enqueue(this);
    }

    public void callFriendListApi(String type, int page) {
        HashMap<String, String> headerMap = new HashMap<>();
        headerMap.put("Authorization", "Bearer " + AppPreferences.getInstance(context).getPreferencesString(AppConstants.ACCESS_TOKEN));
        NetworkManager.getApi().getFriendList(headerMap, type, page).enqueue(this);
    }

    public void inviteFriendToRoomApi(String friendId) {
        HashMap<String, String> headerMap = new HashMap<>();
        headerMap.put("Authorization", "Bearer " + AppPreferences.getInstance(context).getPreferencesString(AppConstants.ACCESS_TOKEN));
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("quiz_id", "");
        jsonObject.addProperty("friend_id", friendId);
        jsonObject.addProperty("channel_id", "");
        jsonObject.addProperty("firebase_token", AppPreferences.getInstance(context).getPreferencesString(AppConstants.FIREBASE_TOKEN));
        NetworkManager.getApi().inviteFriendToRoom(headerMap, jsonObject).enqueue(this);
    }

    public void changeCameraStatusApi(String channelId, String action) {
        HashMap<String, String> headerMap = new HashMap<>();
        headerMap.put("Authorization", "Bearer " + AppPreferences.getInstance(context).getPreferencesString(AppConstants.ACCESS_TOKEN));
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("user_id", AppPreferences.getInstance(context).getPreferencesString(AppConstants.UID));
        jsonObject.addProperty("channel_id", channelId);
        NetworkManager.getApi().changeCameraStatus(headerMap, action, jsonObject).enqueue(this);
    }

    public void callContactFileLink() {
        HashMap<String, String> headerMap = new HashMap<>();
        headerMap.put("Authorization", "Bearer " + AppPreferences.getInstance(context).getPreferencesString(AppConstants.ACCESS_TOKEN));
        NetworkManager.getApi().getContactFileLink(headerMap).enqueue(this);
    }

    public void callUnfriendApi(String friendId, int size) {
        this.size = size;
        this.id = friendId;
        HashMap<String, String> headerMap = new HashMap<>();
        headerMap.put("Authorization", "Bearer " + AppPreferences.getInstance(context).getPreferencesString(AppConstants.ACCESS_TOKEN));
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("friend_id", friendId);
        NetworkManager.getApi().unFriendApi(headerMap, jsonObject).enqueue(this);
    }

    public void onDestroyedView() {
        quizView = null;
    }
}