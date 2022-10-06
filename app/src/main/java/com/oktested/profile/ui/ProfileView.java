package com.oktested.profile.ui;

import com.oktested.core.ui.MasterView;
import com.oktested.dashboard.model.GetUserResponse;
import com.oktested.userDetail.model.UpdateUserResponse;

public interface ProfileView extends MasterView {
    void setUpdateData(UpdateUserResponse updateUserResponse);

    void getUserData(GetUserResponse getUserResponse);
}