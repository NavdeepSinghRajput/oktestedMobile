package com.oktested.dashboard.ui;

import com.oktested.core.ui.MasterView;
import com.oktested.dashboard.model.GetUserResponse;
import com.oktested.home.model.NotifyInviteFriendResponse;
import com.oktested.userDetail.model.UpdateUserResponse;

public interface DashboardView extends MasterView {
    void getUserData(GetUserResponse getUserResponse);

    void setUpdateData(UpdateUserResponse updateUserResponse);

    void setNotifyInviteLink(NotifyInviteFriendResponse notifyInviteFriendResponse);
}