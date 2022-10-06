package com.oktested.home.ui;

import com.oktested.core.ui.MasterView;
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
import com.oktested.home.model.SearchUserContactListResponse;
import com.oktested.home.model.SendFriendRequestResponse;
import com.oktested.home.model.TrendingQuizResponse;
import com.oktested.home.model.UnFriendResponse;
import com.oktested.home.model.UserContactListResponse;
import com.oktested.quizDetail.model.InviteFriendToRoomResponse;
import com.oktested.roomDatabase.model.ContactModel;
import com.oktested.videoCall.model.CameraStatusResponse;

public interface QuizView extends MasterView {

    void setQuizStructureResponse(QuizStructureResponse quizStructureResponse);

    void setTrendingQuizResponse(TrendingQuizResponse trendingQuizResponse);

    void setLatestQuizResponse(LatestQuizResponse latestQuizResponse);

    void setCategoryQuizResponse(CategoryQuizResponse categoryQuizResponse);

    void setCategoryDetailQuizResponse(CategoryDetailQuizResponse categoryDetailQuizResponse);

    void setSearchQuizResponse(SearchQuizResponse searchQuizResponse);

    void setUploadContactResponse(ContactFileUploadResponse contactFileUploadResponse);

    void setContactListResponse(UserContactListResponse userContactListResponse);

    void setSendRequestResponse(SendFriendRequestResponse sendFriendRequestResponse, int position, ContactModel contactModel);

    void setInviteLinkResponse(GenerateInviteLinkResponse generateInviteLinkResponse,String mobileNumber);

    void setFriendListResponse(FriendListResponse friendListResponse);

    void setInviteFriendToRoomResponse(InviteFriendToRoomResponse inviteFriendToRoomResponse);

   /* void setSeacrhContactListResponse(SearchUserContactListResponse searchUserContactListResponse);
*/
    void setContactLinkResponse(ContactFileLinkResponse contactFileLinkResponse);

    void setUnFriendResponse(UnFriendResponse unFriendResponse,int position,String id);

    void setAcceptFriendResponse(AcceptFriendResponse acceptFriendResponse, int position, ContactModel contactModel);

    void setCameraStatusResponse(CameraStatusResponse cameraStatusResponse);
}