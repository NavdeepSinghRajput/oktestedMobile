package com.oktested.quizProfile.ui;

import com.oktested.core.ui.MasterView;
import com.oktested.home.model.CategoryDetailQuizResponse;
import com.oktested.home.model.CategoryQuizResponse;
import com.oktested.home.model.ContactFileUploadResponse;
import com.oktested.home.model.FriendListResponse;
import com.oktested.home.model.GenerateInviteLinkResponse;
import com.oktested.home.model.LatestQuizResponse;
import com.oktested.home.model.SearchQuizResponse;
import com.oktested.home.model.SearchUserContactListResponse;
import com.oktested.home.model.SendFriendRequestResponse;
import com.oktested.home.model.TrendingQuizResponse;
import com.oktested.home.model.UserContactListResponse;
import com.oktested.quizDetail.model.InviteFriendToRoomResponse;
import com.oktested.quizProfile.model.RecentPlayedQuizResponse;
import com.oktested.quizProfile.model.UserStatsResponse;

public interface QuizProfileView extends MasterView {


    void setRecentPlayedQuizResponse(RecentPlayedQuizResponse recentPlayedQuizResponse);

    void setUserStatsResponse(UserStatsResponse userStatsResponse);
}