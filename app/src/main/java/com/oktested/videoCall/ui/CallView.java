package com.oktested.videoCall.ui;

import com.oktested.core.ui.MasterView;
import com.oktested.home.model.FriendListResponse;
import com.oktested.playSolo.model.InsertQuizQuestionResponse;
import com.oktested.playSolo.model.QuizStartResponse;
import com.oktested.quizDetail.model.InviteFriendToRoomResponse;
import com.oktested.quizDetail.model.QuizDetailResponse;
import com.oktested.quizResult.model.GroupQuizResultResponse;
import com.oktested.videoCall.model.AcceptSuggestedQuizResponse;
import com.oktested.videoCall.model.CameraStatusResponse;
import com.oktested.videoCall.model.ChannelDetailResponse;
import com.oktested.videoCall.model.LeaveRoomResponse;
import com.oktested.videoCall.model.QuestionWiseResultResponse;
import com.oktested.videoCall.model.RecommendedQuizResponse;
import com.oktested.videoCall.model.RemoveParticipantResponse;

public interface CallView extends MasterView {
    void setPlayGroupResponse(QuizStartResponse quizStartResponse);

    void insertQuizDataResponse(InsertQuizQuestionResponse insertQuizQuestionResponse);

    void setLeaveRoomResponse(LeaveRoomResponse leaveRoomResponse);

    void setQuestionWiseResultResponse(QuestionWiseResultResponse questionWiseResultResponse);

    void setGroupQuizResultResponse(GroupQuizResultResponse groupQuizResultResponse);

    void setFriendListResponse(FriendListResponse friendListResponse);

    void setInviteFriendToRoomResponse(InviteFriendToRoomResponse inviteFriendToRoomResponse);

    void setRecommendedQuizResponse(RecommendedQuizResponse recommendedQuizResponse);

    void setQuizDetailResponse(QuizDetailResponse quizDetailResponse);

    void setAcceptSuggestedQuizResponse(AcceptSuggestedQuizResponse acceptSuggestedQuizResponse);

    void setChannelDetailResponse(ChannelDetailResponse channelDetailResponse);

    void setRemoveParticipantResponse(RemoveParticipantResponse removeParticipantResponse);

    void setCameraStatusResponse(CameraStatusResponse cameraStatusResponse);
}