package com.oktested.quizDetail.ui;

import com.oktested.core.ui.MasterView;
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
import com.oktested.roomDatabase.model.ContactModel;
import com.oktested.videoCall.model.CameraStatusResponse;

public interface QuizDetailView extends MasterView {

    void setQuizDetailResponse(QuizDetailResponse quizDetailResponse);

    void setInviteFriendToRoomResponse(InviteFriendToRoomResponse inviteFriendToRoomResponse);

    void setFriendListResponse(FriendListResponse friendListResponse);

    void setContactListResponse(UserContactListResponse userContactListResponse);

    void setUploadContactResponse(ContactFileUploadResponse contactFileUploadResponse);

    void setSendRequestResponse(SendFriendRequestResponse sendFriendRequestResponse, int position, ContactModel contactModel);

    void setInviteLinkResponse(GenerateInviteLinkResponse generateInviteLinkResponse, String mobileNumber);

    void setContactLinkResponse(ContactFileLinkResponse contactFileLinkResponse);

    void setUnFriendResponse(UnFriendResponse unFriendResponse, int position,String id);

    void setAcceptFriendResponse(AcceptFriendResponse notifcationModelItem, int position,ContactModel contactModel);

    void setCameraStatusResponse(CameraStatusResponse cameraStatusResponse);
}